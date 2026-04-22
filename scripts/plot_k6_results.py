#!/usr/bin/env python3
import argparse
import json
import os
from pathlib import Path

os.environ.setdefault("MPLCONFIGDIR", "/tmp/airline-matplotlib-cache")
os.environ.setdefault("XDG_CACHE_HOME", "/tmp/airline-cache")

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt


def parse_args():
    parser = argparse.ArgumentParser(description="Build a simple response-time chart from k6 JSON output.")
    parser.add_argument("--input", default="reports/k6-result.json", help="Path to k6 JSON output.")
    parser.add_argument("--output", default="reports/k6-response-time.png", help="Path to output PNG chart.")
    return parser.parse_args()


def read_duration_points(path):
    points = []
    with Path(path).open(encoding="utf-8") as source:
        for line in source:
            if not line.strip():
                continue
            record = json.loads(line)
            if record.get("type") != "Point":
                continue
            metric = record.get("metric")
            if metric != "http_req_duration":
                continue
            data = record.get("data", {})
            points.append(float(data.get("value", 0)))
    return points


def main():
    args = parse_args()
    points = read_duration_points(args.input)
    if not points:
        raise SystemExit("No http_req_duration points found. Run k6 with: --out json=reports/k6-result.json")

    output = Path(args.output)
    output.parent.mkdir(parents=True, exist_ok=True)

    plt.figure(figsize=(10, 5))
    plt.plot(range(1, len(points) + 1), points)
    plt.title("k6 http_req_duration")
    plt.xlabel("request point")
    plt.ylabel("duration, ms")
    plt.grid(True)
    plt.tight_layout()
    plt.savefig(output)

    print(f"Saved chart to {output}")


if __name__ == "__main__":
    main()
