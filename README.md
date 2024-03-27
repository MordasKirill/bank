## Задание
Необходимо реализовать приложение, которое позволяет управлять счетами пользователя в банке и выполнять транзакции по переводу средств.

Пользователь взаимодействует с приложением через консоль/терминал.
Формат команд: command param1 param2 ... paramN

**COMPLETED** -Должна быть команда, которая выводит список команд с их описанием.

## Перечень функций приложения:
**COMPLETED** - crud операции по управлению банками

**COMPLETED** - crud операции по добавлению/управлению клиентами банка

**COMPLETED** - примечание: при добавлении клиента в банк, нужно открыть хотя бы один счет в этом банке

**COMPLETED** - клиенты банка могут переводить средства на счета других клиентов того же самого банка без комиссии

**COMPLETED** - клиенты банка могут переводить средства на счета клиентов других банков с фиксированной комиссией (комиссия указывается при добавлении банка)

**COMPLETED** - существуют 2 вида клиентов, физические и юридические, банки взимают разные комиссии для разных видов клиентов

**COMPLETED** - клиент может иметь счета в разных банках

**Different currency - done, but as for now it does not influence on the transfer.** - каждый счет имеет свою валюту, которая должна учитываться при переводах

**COMPLETED** - приложение должно позволять выводить счета клиента и кол-во средств на них

**COMPLETED** - приложение должно позволять выводить все транзакции, проведенные клиентом за указанный период

**COMPLETED** - данные можно хранить либо в json-файле (библиотека jackson) либо в базе данных (соответствующий jdbc драйвер)
