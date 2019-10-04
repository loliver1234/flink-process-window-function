## Overview

Purpose of this codebase is to to provide reproducible example of a bug on Apache Flink®

Bug description: https://issues.apache.org/jira/browse/FLINK-14197

---
## Flink job overview
What this job does is simply emmiting messages gathered from rabbitmq queue and send them back.

RabbitMQ source with Exactly-once guarantee and 65k prefetch count.

Stream time characteristic set to TimeCharacteristic.ProcessingTime.

There is no user managed state.

Window gap set to: `ProcessingTimeSessionWindows.withGap(Time.milliseconds(100))`

Checkpointing configuration is set to use RocksDB state backend with incremental checkpointing and EXACTLY_ONCE mode

---
## Steps to reproduce

1. Start rabbitmq broker:
```
$ ./rabbit/start_rabbit.sh
```
2. Start flink job from IDE
3. Start messages consumer with ./data/consume_messages.py that is counting messages consumed.
4. Start sending messages with ./data/send_messages.py python script.
5. Observe state size of Process window function operator and it's increasing trend from flink web console.
6. State is not decreasing after all messages are consumed and processed

---
## Outcome results
### Scenario 1
- Unique keys: 100
- Initial state: 15.8 KB
- Message size: 106 B

| Messages sent overall | Messages sent per key | State size |
| ---------------------:| ---------------------:| ----------:|
| 10000                 | 100                   | 128.8 KB   |
| 100000                | 1000                  | 142.4 KB   |
| 1000000               | 10000                 | 264 KB     |

---
### Scenario 2
- Unique keys: 1000
- Initial state: 15.8 KB
- Message size: 106 B

| Messages sent overall | Messages sent per key | State size |
| ---------------------:| ---------------------:| ----------:|
| 10000                 | 10                    | 128.8 KB   |
| 100000                | 100                   | 142.4 KB   |
| 1000000               | 1000                  | 267.2 KB   |

---
### Scenario 3
- Unique keys: 10000
- Initial state: 15.8 KB
- Message size: 106 B

| Messages sent overall | Messages sent per key | State size |
| ---------------------:| ---------------------:| ----------:|
| 10000                 | 1                     | 128.8 KB   |
| 100000                | 10                    | 144 KB     |
| 1000000               | 100                   | 271.2 KB   |

---
### Scenario 4
- Unique keys: 100
- Initial state: 15.8 KB
- Message size: 1060 B

| Messages sent overall | Messages sent per key | State size |
| ---------------------:| ---------------------:| ----------:|
| 10000                 | 100                   | 128.8 KB   |
| 100000                | 1000                  | 147.2 KB   |
| 1000000               | 10000                 | 306.4 KB   |

---
### Scenario 5
- Unique keys: 1000
- Initial state: 15.8 KB
- Message size: 1060 B

| Messages sent overall | Messages sent per key | State size |
| ---------------------:| ---------------------:| ----------:|
| 10000                 | 10                    | 128.8 KB   |
| 100000                | 100                   | 148 KB     |
| 1000000               | 1000                  | 306 KB     |

---
### Scenario 6
- Unique keys: 10000
- Initial state: 15.8 KB
- Message size: 1060 B

| Messages sent overall | Messages sent per key | State size |
| ---------------------:| ---------------------:| ----------:|
| 10000                 | 1                     | 130 KB     |
| 100000                | 10                    | 149 KB     |
| 1000000               | 100                   | 313 KB     |
