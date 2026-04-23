#!/usr/bin/env python3
import argparse
import json
from collections import defaultdict
from pathlib import Path

import os

os.environ.setdefault("MPLCONFIGDIR", "/tmp/airline-matplotlib-cache")
os.environ.setdefault("XDG_CACHE_HOME", "/tmp/airline-cache")

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt


def parse_args():
    parser = argparse.ArgumentParser(
        description="Строит график времени отклика от CPU для нескольких соотношений запись/чтение."
    )
    parser.add_argument(
        "--result",
        action="append",
        required=True,
        help="Запись в формате ratio@cpu=path, например 5/95@0.5=reports/lab6-0.5-5-95.json",
    )
    parser.add_argument(
        "--output",
        default="reports/lab6-cpu-response-time.png",
        help="Куда сохранить итоговый PNG.",
    )
    return parser.parse_args()


def parse_result_argument(raw_value):
    try:
        label, path = raw_value.split("=", 1)
        ratio, cpu_raw = label.split("@", 1)
        cpu = float(cpu_raw)
    except ValueError as exc:
        raise SystemExit(
            f"Некорректный аргумент --result '{raw_value}'. Ожидается ratio@cpu=path"
        ) from exc

    return ratio, cpu, Path(path)


def average_http_req_duration(path):
    total = 0.0
    count = 0

    with path.open(encoding="utf-8") as source:
        for line in source:
            if not line.strip():
                continue

            record = json.loads(line)
            if record.get("type") != "Point" or record.get("metric") != "http_req_duration":
                continue

            total += float(record.get("data", {}).get("value", 0.0))
            count += 1

    if count == 0:
        raise SystemExit(f"В файле {path} не найдены точки http_req_duration.")

    return total / count


def main():
    args = parse_args()
    output = Path(args.output)
    output.parent.mkdir(parents=True, exist_ok=True)

    series = defaultdict(list)
    for raw_result in args.result:
        ratio, cpu, path = parse_result_argument(raw_result)
        series[ratio].append((cpu, average_http_req_duration(path)))

    plt.figure(figsize=(10, 6))
    for ratio, points in sorted(series.items()):
        points.sort(key=lambda item: item[0])
        cpus = [cpu for cpu, _ in points]
        averages = [avg for _, avg in points]
        plt.plot(cpus, averages, marker="o", label=f"Вставка/чтение {ratio}")

        for cpu, avg in points:
            plt.annotate(
                f"{avg:.1f} мс",
                (cpu, avg),
                textcoords="offset points",
                xytext=(0, 8),
                ha="center",
            )

    plt.title("LAB6: время отклика от количества CPU")
    plt.xlabel("CPU контейнера приложения")
    plt.ylabel("Среднее время ответа (мс)")
    plt.grid(True)
    plt.legend()
    plt.tight_layout()
    plt.savefig(output)
    print(f"График сохранён в {output}")


if __name__ == "__main__":
    main()
