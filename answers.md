# Ответы на вопросы к защите лабораторной работы №5

---

## 1. Коллекции. Сортировка элементов коллекции. Интерфейсы `java.util.Comparable` и `java.util.Comparator`

### Коллекции

**Коллекция** (Collection) — это объект, представляющий собой группу элементов. Java Collections Framework (JCF) — это унифицированная архитектура для представления и работы с коллекциями, включающая интерфейсы, реализации и алгоритмы.

Корневой интерфейс иерархии — `java.util.Collection<E>`, от которого наследуются `List`, `Set`, `Queue`. Отдельно стоит интерфейс `Map<K, V>`, не наследующий `Collection`, но являющийся частью JCF.

### Сортировка элементов коллекции

Сортировка доступна не для всех типов коллекций:

- **Списки (`List`)** — сортируются напрямую через `Collections.sort(list)` или `list.sort(comparator)`.
- **Множества (`Set`)** — `TreeSet` автоматически поддерживает порядок; `HashSet` и `LinkedHashSet` не гарантируют порядка.
- **Отображения (`Map`)** — **`Hashtable` не поддерживает сортировку напрямую**, так как не гарантирует порядок ключей. Для сортировки значений `Hashtable` необходимо:
  1. Извлечь записи в `List<Map.Entry<K,V>>`.
  2. Отсортировать этот список через `Collections.sort()` или `List.sort()`.
  3. Работать с отсортированным списком.

Именно это используется в проекте:

```java
// PrintFieldDescendingDifficultyCommand.java
List<Map.Entry<Integer, LabWork>> entries = new ArrayList<>();
for (Map.Entry<Integer, LabWork> entry : coll.entrySet()) {
    if (entry.getValue().getDifficulty() != null) {
        entries.add(entry);
    }
}
entries.sort((a, b) -> b.getValue().getDifficulty().compareTo(a.getValue().getDifficulty()));
```

Здесь записи из `Hashtable` копируются в `ArrayList`, а затем сортируются лямбда-выражением (анонимным `Comparator`).

### Интерфейс `java.util.Comparable<T>`

Определяет **естественный порядок сортировки** объектов. Содержит единственный метод:

```java
int compareTo(T o);
```

Возвращает:
- **отрицательное число** — если текущий объект меньше `o`;
- **0** — если объекты равны;
- **положительное число** — если текущий объект больше `o`.

В проекте класс `LabWork` реализует `Comparable<LabWork>`:

```java
public class LabWork implements Comparable<LabWork> {
    @Override
    public int compareTo(LabWork labWork) {
        return Long.compare(this.maximumPoint, labWork.maximumPoint);
    }
}
```

Это позволяет сравнивать лабораторные работы по полю `maximumPoint` (используется, например, в `ReplaceGreatestCommand` и `ReplaceLowestCommand`).

### Интерфейс `java.util.Comparator<T>`

Определяет **внешний порядок сортировки**, не привязанный к самому классу. Содержит метод:

```java
int compare(T o1, T o2);
```

Может быть реализован через:
- отдельный класс;
- анонимный класс;
- лямбда-выражение (т.к. это функциональный интерфейс — `@FunctionalInterface`).

В проекте `Comparator` применяется через лямбда-выражения:

```java
// сортировка по difficulty в обратном порядке
entries.sort((a, b) -> b.getValue().getDifficulty().compareTo(a.getValue().getDifficulty()));
```

### Отличия `Comparable` от `Comparator`

| Критерий | `Comparable` | `Comparator` |
|---|---|---|
| Пакет | `java.lang` | `java.util` |
| Метод | `compareTo(T o)` | `compare(T o1, T o2)` |
| Модифицирует класс | Да (класс реализует интерфейс) | Нет (внешний объект) |
| Количество порядков | Один (естественный) | Множество (любое число компараторов) |
| Использование | `Collections.sort(list)` | `Collections.sort(list, comparator)` |

---

## 2. Категории коллекций. Интерфейсы `List`, `Set`, `Queue`, `Map` и их реализации

### Списки — `java.util.List<E>`

Упорядоченная коллекция с доступом по индексу. Допускает дубликаты.

| Реализация | Структура | Особенности |
|---|---|---|
| `ArrayList` | Динамический массив | Быстрый доступ по индексу — O(1); вставка/удаление в середину — O(n) |
| `LinkedList` | Двусвязный список | Быстрая вставка/удаление — O(1) в начале/конце; доступ по индексу — O(n) |
| `Vector` | Синхронизированный массив | Потокобезопасный, но медленнее `ArrayList` |

В проекте `ArrayList` используется для хранения аргументов команд и промежуточных списков:

```java
ArrayList<String> args = new ArrayList<>();          // командные аргументы
List<Map.Entry<Integer, LabWork>> entries = new ArrayList<>();  // для сортировки
```

### Множества — `java.util.Set<E>`

Коллекция без дубликатов.

| Реализация | Структура | Особенности |
|---|---|---|
| `HashSet` | Хеш-таблица | Быстрые операции — O(1); порядок не гарантирован |
| `LinkedHashSet` | Хеш-таблица + связный список | Сохраняет порядок вставки |
| `TreeSet` | Красно-чёрное дерево | Автоматическая сортировка; операции — O(log n) |

В проекте `HashSet` используется для получения уникальных авторов:

```java
// UniqueAuthorCommand.java
Set<Person> unique_authors = new HashSet<>();
for (LabWork lab : coll.values()) {
    unique_authors.add(lab.getAuthor());
}
```

### Очереди — `java.util.Queue<E>`

Коллекция, организованная по принципу FIFO (первый вошёл — первый вышел).

| Реализация | Особенности |
|---|---|
| `LinkedList` | Реализует и `List`, и `Queue` |
| `PriorityQueue` | Элементы извлекаются в порядке приоритета (по `Comparable` или `Comparator`) |
| `ArrayDeque` | Двусторонняя очередь на базе массива, быстрее `LinkedList` |

Подинтерфейс `Deque<E>` расширяет `Queue`, позволяя добавлять и извлекать элементы с обоих концов.

### Отображения (словари) — `java.util.Map<K, V>`

Коллекция пар «ключ → значение». Ключи уникальны.

| Реализация | Структура | Особенности |
|---|---|---|
| `HashMap` | Хеш-таблица | Не потокобезопасен, допускает `null`-ключ; порядок не гарантирован |
| **`Hashtable`** | Хеш-таблица | **Потокобезопасный** (synchronized), не допускает `null`-ключей и `null`-значений; порядок не гарантирован |
| `LinkedHashMap` | Хеш-таблица + связный список | Сохраняет порядок вставки |
| `TreeMap` | Красно-чёрное дерево | Автоматическая сортировка ключей; O(log n) |

В проекте используется `Hashtable<Integer, LabWork>` в качестве основной коллекции (по заданию):

```java
// CollectionManager.java
private static Hashtable<Integer, LabWork> labworks = FileEditor.getCollection();
```

А также `HashMap<String, Command>` для хранения команд и `TreeMap<Long, Integer>` для группировки по `maximumPoint`:

```java
// CommandInvoker.java
private static HashMap<String, Command> commands = new HashMap<>();

// GroupCountingByMaximumPointCommand.java
Map<Long, Integer> groups = new TreeMap<>();  // TreeMap для автосортировки по ключу
```

---

## 3. Параметризованные типы. Создание параметризуемых классов. Wildcard-параметры

### Параметризованные типы (Generics)

**Дженерики** (обобщения) — механизм Java, позволяющий создавать классы, интерфейсы и методы, работающие с различными типами данных при сохранении типобезопасности на этапе компиляции. Были введены в Java 5.

**Преимущества:**
- **Типобезопасность на этапе компиляции** — ошибки обнаруживаются до запуска программы.
- **Устранение необходимости приведения типов** — компилятор сам вставляет нужные приведения.
- **Повторное использование кода** — один параметризованный класс работает с разными типами.

**Синтаксис:**

```java
public class Box<T> {
    private T content;
    public void set(T content) { this.content = content; }
    public T get() { return content; }
}

Box<String> stringBox = new Box<>();  // Diamond-оператор (<>)
stringBox.set("Hello");
String s = stringBox.get();  // без приведения типов
```

### Применение в проекте

Практически все коллекции в проекте параметризованы:

```java
Hashtable<Integer, LabWork>     // основная коллекция
HashMap<String, Command>        // реестр команд
ArrayList<String>               // аргументы команд
TreeMap<Long, Integer>          // группировка
HashSet<Person>                 // уникальные авторы
List<Map.Entry<Integer, LabWork>>  // вложенная параметризация
```

Интерфейс `Comparable` также параметризован:

```java
public class LabWork implements Comparable<LabWork> { ... }
```

### Создание параметризуемых классов

```java
public class Pair<K, V> {         // два параметра типа
    private K key;
    private V value;
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    public K getKey() { return key; }
    public V getValue() { return value; }
}
```

**Ограничения параметров типа (bounded type parameters):**

```java
// T должен быть подклассом Number
public class NumberBox<T extends Number> {
    private T value;
    public double doubleValue() { return value.doubleValue(); }
}

// Множественные ограничения
public <T extends Comparable<T> & Serializable> void sort(List<T> list) { ... }
```

### Wildcard-параметры (`?`)

Подстановочные символы используются, когда точный тип не важен или неизвестен.

| Тип | Синтаксис | Описание | Чтение/Запись |
|---|---|---|---|
| Неограниченный | `<?>` | Любой тип | Только чтение (`Object`) |
| С верхней границей | `<? extends T>` | T или его подклассы | Только чтение (как T) |
| С нижней границей | `<? super T>` | T или его суперклассы | Запись T; чтение как `Object` |

**Примеры:**

```java
// Принимает список любых Number и его подклассов (Integer, Double, ...)
public static double sum(List<? extends Number> list) {
    double sum = 0;
    for (Number n : list) sum += n.doubleValue();
    return sum;
}

// Принимает список Integer и его суперклассов (Number, Object)
public static void addIntegers(List<? super Integer> list) {
    list.add(1);
    list.add(2);
}
```

**Принцип PECS** (Producer Extends, Consumer Super):
- Если коллекция **производит** данные (читаем из неё) → `extends`.
- Если коллекция **потребляет** данные (пишем в неё) → `super`.

### Стирание типов (Type Erasure)

При компиляции дженерики «стираются» — параметры типов заменяются на `Object` (или на верхнюю границу). Это означает:
- Нельзя создать экземпляр параметра типа: `new T()` — ошибка.
- Нельзя использовать `instanceof` с параметром типа.
- Нельзя создать массив параметризованного типа: `new List<String>[10]` — ошибка.

---

## 4. Классы-оболочки. Назначение, область применения, преимущества и недостатки. Автоупаковка и автораспаковка

### Классы-оболочки (Wrapper Classes)

Для каждого примитивного типа в Java существует соответствующий класс-оболочка:

| Примитив | Класс-оболочка | Размер |
|---|---|---|
| `byte` | `Byte` | 1 байт |
| `short` | `Short` | 2 байта |
| `int` | `Integer` | 4 байта |
| `long` | `Long` | 8 байт |
| `float` | `Float` | 4 байта |
| `double` | `Double` | 8 байт |
| `char` | `Character` | 2 байта |
| `boolean` | `Boolean` | ~1 байт |

### Назначение

1. **Использование в коллекциях** — дженерики не работают с примитивами, только с объектами.
2. **Возможность хранить `null`** — примитив не может быть `null`, а оболочка может.
3. **Утилитарные методы** — `Integer.parseInt()`, `Double.valueOf()`, `Long.compare()` и т.д.
4. **Работа с рефлексией и сериализацией**.

### Применение в проекте

```java
// Поля классов (nullable-типы используют оболочки)
private Long id;              // может быть null, автозаполняется
private Float minimalPoint;   // может быть null
private Integer x;            // Coordinates: не может быть null, но нужен объект для коллекций

// Примитивы используются, когда null не требуется
private long maximumPoint;    // всегда > 0, null невозможен
private float y;              // Location
private double z;             // Location

// Коллекции параметризованы оболочками
Hashtable<Integer, LabWork>   // Integer, а не int
TreeMap<Long, Integer>        // Long и Integer
```

### Преимущества

- Позволяют использовать примитивы в обобщённых типах.
- Поддерживают `null` (важно для полей, которые могут отсутствовать).
- Предоставляют полезные статические методы (`parseInt`, `compare`, `valueOf` и т.д.).
- Являются неизменяемыми (immutable) и потокобезопасными.

### Недостатки

- **Большее потребление памяти** — объект `Integer` занимает ~16 байт против 4 байт для `int`.
- **Медленнее при частых операциях** — из-за создания объектов и сборки мусора.
- **Риск `NullPointerException`** при распаковке `null`.
- **Сравнение через `==`** сравнивает ссылки, а не значения (надо использовать `.equals()`).

### Автоупаковка (Autoboxing) и автораспаковка (Unboxing)

Начиная с Java 5, компилятор автоматически преобразует примитивы в оболочки и обратно.

**Автоупаковка** (autoboxing) — автоматическое преобразование примитива в соответствующий объект-оболочку:

```java
Integer a = 42;          // компилятор: Integer a = Integer.valueOf(42);
List<Integer> list = new ArrayList<>();
list.add(10);            // компилятор: list.add(Integer.valueOf(10));
```

**Автораспаковка** (unboxing) — автоматическое преобразование объекта-оболочки обратно в примитив:

```java
Integer a = Integer.valueOf(42);
int b = a;               // компилятор: int b = a.intValue();
int sum = a + 10;        // компилятор: int sum = a.intValue() + 10;
```

В проекте автоупаковка/распаковка происходит повсеместно:

```java
// Long.compare() принимает примитивы, maximumPoint — примитив long
return Long.compare(this.maximumPoint, labWork.maximumPoint);

// Integer.parseInt() возвращает int, а key — Integer (автоупаковка)
Integer key = Integer.parseInt(args.get(0));

// groups.put(mp, ...) — mp это long, а ключ TreeMap — Long (автоупаковка)
groups.put(mp, groups.getOrDefault(mp, 0) + 1);
```

### Кэширование оболочек

Java кэширует значения `Integer` от -128 до 127, `Boolean`, `Byte` и маленькие `Short`/`Long`:

```java
Integer a = 127;
Integer b = 127;
System.out.println(a == b);  // true (один и тот же объект из кэша)

Integer c = 128;
Integer d = 128;
System.out.println(c == d);  // false (разные объекты!)
System.out.println(c.equals(d));  // true (сравнение значений)
```

---

## 5. Потоки ввода-вывода в Java. Байтовые и символьные потоки. «Цепочки» потоков (Stream Chains)

### Модель потоков (Streams)

В Java ввод-вывод организован на основе потоков (streams). Поток — это абстрактная последовательность данных. Данные читаются из **источника** (файл, сеть, массив) и записываются в **приёмник**.

### Байтовые потоки

Работают с **сырыми байтами** (8-битными единицами данных). Базовые абстрактные классы:

| Класс | Назначение |
|---|---|
| `InputStream` | Базовый класс для чтения байтов |
| `OutputStream` | Базовый класс для записи байтов |

Основные реализации:

| Класс | Описание |
|---|---|
| `FileInputStream` | Чтение байтов из файла |
| `FileOutputStream` | Запись байтов в файл |
| `ByteArrayInputStream` | Чтение из массива байтов |
| `BufferedInputStream` | Буферизированное чтение (повышает производительность) |
| `DataInputStream` | Чтение примитивных типов из потока |

В проекте `FileInputStream` используется в `FileEditor`:

```java
InputStream inputStream = new FileInputStream(file);
```

### Символьные потоки

Работают с **символами** (16-битными единицами, поддержка Unicode). Базовые абстрактные классы:

| Класс | Назначение |
|---|---|
| `Reader` | Базовый класс для чтения символов |
| `Writer` | Базовый класс для записи символов |

Основные реализации:

| Класс | Описание |
|---|---|
| `InputStreamReader` | Мост от байтового потока к символьному (декодирование) |
| `OutputStreamWriter` | Мост от символьного потока к байтовому (кодирование) |
| `FileReader` | Чтение символов из файла |
| `FileWriter` | Запись символов в файл |
| `BufferedReader` | Буферизированное чтение символов |
| `BufferedWriter` | Буферизированная запись символов |

В проекте используются:

```java
// Чтение — InputStreamReader (мост: байты → символы с указанием кодировки)
InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

// Запись — FileWriter (символьный поток в файл)
FileWriter writer = new FileWriter(DATA_FILE_NAME, StandardCharsets.UTF_8);
```

### «Цепочки» потоков (Stream Chains)

Ключевая концепция Java I/O — потоки можно «оборачивать» друг в друга, создавая цепочки. Каждый слой добавляет функциональность.

**Пример цепочки из проекта (чтение):**

```
FileInputStream          →  InputStreamReader       →  XmlMapper.readValue()
(байтовый поток из файла)   (декодирование UTF-8)       (десериализация XML)
```

```java
// FileEditor.java — цепочка для чтения
InputStream inputStream = new FileInputStream(file);             // 1. Байтовый поток из файла
InputStreamReader reader = new InputStreamReader(inputStream,    // 2. Декодирование в символы
                                                 StandardCharsets.UTF_8);
Hashtable<String, LabWork> raw = xmlMapper.readValue(reader, ...); // 3. Десериализация XML
```

**Пример цепочки (запись):**

```java
// FileEditor.java — запись
FileWriter writer = new FileWriter(DATA_FILE_NAME, StandardCharsets.UTF_8);  // символьный поток
xmlMapper.writerWithDefaultPrettyPrinter().writeValue(writer, wrapped);       // сериализация XML
```

**Ещё пример из `CommandInvoker` (чтение скриптов):**

```java
FileInputStream fis = new FileInputStream(f);                // 1. Байтовый поток
InputStreamReader isr = new InputStreamReader(fis, "UTF-8"); // 2. Декодирование
Scanner sc = new Scanner(isr);                                // 3. Разбор ввода
```

### Паттерн «Декоратор»

Цепочки потоков реализуют паттерн «Декоратор» — каждый поток-обёртка добавляет поведение (буферизация, кодирование, шифрование), не изменяя интерфейс.

### `try-with-resources`

Потоки реализуют интерфейс `AutoCloseable`, что позволяет использовать конструкцию `try-with-resources` для автоматического закрытия:

```java
try (InputStream inputStream = new FileInputStream(file);
     InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
    // работа с потоком
}  // потоки автоматически закрываются
```

Используется в проекте в `FileEditor.getCollection()` и `FileEditor.saveCollection()`.

---

## 6. Работа с файлами в Java. Класс `java.io.File`

### Класс `java.io.File`

`File` — класс, представляющий абстрактный путь к файлу или директории в файловой системе. **Не открывает и не изменяет содержимое файла** — только работает с метаданными и путями.

### Основные возможности

| Метод | Описание |
|---|---|
| `exists()` | Проверяет существование файла/директории |
| `isFile()` / `isDirectory()` | Определяет тип |
| `getName()` | Возвращает имя файла |
| `getAbsolutePath()` | Полный путь |
| `length()` | Размер файла в байтах |
| `canRead()` / `canWrite()` | Проверка прав доступа |
| `createNewFile()` | Создаёт новый пустой файл |
| `delete()` | Удаляет файл/директорию |
| `mkdir()` / `mkdirs()` | Создаёт директорию/директории |
| `list()` / `listFiles()` | Содержимое директории |
| `renameTo(File)` | Переименование/перемещение |

### Применение в проекте

```java
// FileEditor.java — создание объекта File для работы с данными
File file = new File(DATA_FILE_NAME);  // "data.xml"

// ExecuteCommand.java — открытие файла скрипта
File file = new File(fileName);
CommandInvoker.runFile(file);

// FileEditor.java — передача File в FileInputStream
InputStream inputStream = new FileInputStream(file);
```

### Примеры использования

```java
File f = new File("data.xml");

if (f.exists()) {
    System.out.println("Размер: " + f.length() + " байт");
    System.out.println("Путь: " + f.getAbsolutePath());
    System.out.println("Доступен для чтения: " + f.canRead());
    System.out.println("Доступен для записи: " + f.canWrite());
}

// Работа с директориями
File dir = new File("output");
if (!dir.exists()) {
    dir.mkdirs();
}
```

### Ограничения `java.io.File`

- Методы не выбрасывают исключений — возвращают `false` при ошибке, не объясняя причину.
- Не поддерживает символические ссылки.
- Нет поддержки атрибутов файлов (permissions, владелец).
- Не атомарные операции.

Для устранения этих ограничений была создана новая API — `java.nio.file` (см. вопрос 7).

---

## 7. Пакет `java.nio` — назначение, основные классы и интерфейсы

### Назначение

Пакет `java.nio` (New I/O) был добавлен в Java 1.4, а значительно расширен в Java 7 (NIO.2, пакет `java.nio.file`). Предназначен для:

1. **Более эффективного ввода-вывода** — неблокирующий (non-blocking) I/O, каналы и буферы.
2. **Улучшенной работы с файловой системой** — замена `java.io.File` классами `Path`, `Files`, `FileSystem`.
3. **Поддержки атрибутов файлов**, символических ссылок, мониторинга изменений.

### Основные классы и интерфейсы

#### Буферы (`java.nio.Buffer`)

Буфер — контейнер данных фиксированного размера. Основные реализации:

| Класс | Тип данных |
|---|---|
| `ByteBuffer` | Байты |
| `CharBuffer` | Символы |
| `IntBuffer` | Целые числа |
| `LongBuffer` | Длинные целые |
| `FloatBuffer` / `DoubleBuffer` | Числа с плавающей точкой |

Ключевые свойства буфера: `capacity`, `position`, `limit`, `mark`.

#### Каналы (`java.nio.channels.Channel`)

Канал — двунаправленный поток данных (в отличие от однонаправленных потоков I/O).

| Класс | Описание |
|---|---|
| `FileChannel` | Ввод-вывод из файлов |
| `SocketChannel` | TCP-соединения |
| `ServerSocketChannel` | Прослушивание TCP |
| `DatagramChannel` | UDP |
| `Selector` | Мультиплексирование каналов (позволяет одному потоку обслуживать несколько каналов) |

#### Файловая система NIO.2 (`java.nio.file`)

| Класс/Интерфейс | Описание |
|---|---|
| `Path` | Представление файлового пути (замена `File`) |
| `Paths` | Фабрика для создания `Path` |
| `Files` | Утилитарный класс со статическими методами для операций с файлами |
| `FileSystem` | Абстракция файловой системы |
| `FileSystems` | Фабрика для `FileSystem` |
| `FileVisitor` | Интерфейс для обхода дерева файлов |
| `WatchService` | Мониторинг изменений в директории |

#### Кодировки (`java.nio.charset`)

| Класс | Описание |
|---|---|
| `Charset` | Представление набора символов |
| `CharsetEncoder` | Кодирование символов в байты |
| `CharsetDecoder` | Декодирование байтов в символы |
| `StandardCharsets` | Константы стандартных кодировок (UTF_8, ISO_8859_1 и др.) |

### Применение в проекте

```java
// FileEditor.java — использование java.nio.charset для указания кодировки
import java.nio.charset.StandardCharsets;

InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
FileWriter writer = new FileWriter(DATA_FILE_NAME, StandardCharsets.UTF_8);

// FileEditor.java — использование java.nio.file для чтения атрибутов файла
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

BasicFileAttributes fileAttributes = Files.readAttributes(
    Path.of(DATA_FILE_NAME),     // Path — представление пути
    BasicFileAttributes.class     // атрибуты: время создания, размер и т.д.
);
Date creationTime = new Date(fileAttributes.creationTime().toMillis());
```

### Сравнение `java.io.File` и `java.nio.file.Path`

| Критерий | `File` | `Path` |
|---|---|---|
| Пакет | `java.io` | `java.nio.file` |
| Тип | Класс | Интерфейс |
| Обработка ошибок | Возвращает `false` | Выбрасывает конкретные исключения |
| Символические ссылки | Не поддерживает | Полная поддержка |
| Атрибуты файлов | Ограниченная поддержка | `Files.readAttributes()` |
| Мониторинг изменений | Нет | `WatchService` |

---

## 8. Утилита `javadoc`. Особенности автоматического документирования кода в Java

### Утилита `javadoc`

`javadoc` — инструмент из JDK, который автоматически генерирует HTML-документацию из исходного кода Java на основе специальных комментариев.

**Запуск:**

```bash
mvn -DskipTests javadoc:javadoc
```

### Javadoc-комментарии

Javadoc-комментарий начинается с `/**` и заканчивается `*/`. Размещается непосредственно перед документируемым элементом (класс, метод, поле).

```java
/**
 * Краткое описание (первое предложение — summary).
 * <p>
 * Подробное описание, может содержать HTML-теги.
 * </p>
 *
 * @тег описание
 */
```

### Основные теги

| Тег | Назначение | Пример |
|---|---|---|
| `@param` | Описание параметра метода | `@param name имя автора` |
| `@return` | Описание возвращаемого значения | `@return количество элементов` |
| `@throws` / `@exception` | Описание выбрасываемого исключения | `@throws IOException при ошибке чтения` |
| `@see` | Ссылка на другой элемент | `@see CollectionManager` |
| `@since` | Версия появления | `@since 1.0` |
| `@deprecated` | Пометка как устаревший | `@deprecated Используйте newMethod()` |
| `@author` | Автор | `@author Akira` |
| `@version` | Версия | `@version 1.0` |
| `{@link}` | Встроенная ссылка | `{@link LabWork#compareTo}` |
| `{@code}` | Код в тексте (не интерпретируется как HTML) | `{@code null}` |

### Применение в проекте

Весь проект задокументирован в формате javadoc. Примеры:

**Документация класса:**

```java
/**
 * Класс, представляющий лабораторную работу.
 * <p>
 * Является основным элементом коллекции, управляемой программой.
 * Реализует интерфейс {@link Comparable} для сортировки по умолчанию
 * по полю maximumPoint.
 * </p>
 */
public class LabWork implements Comparable<LabWork> { ... }
```

**Документация метода:**

```java
/**
 * Сравнивает эту лабораторную работу с другой по полю maximumPoint.
 *
 * @param labWork другая лабораторная работа для сравнения
 * @return отрицательное число, ноль или положительное число, если эта работа
 *         меньше, равна или больше заданной
 */
@Override
public int compareTo(LabWork labWork) {
    return Long.compare(this.maximumPoint, labWork.maximumPoint);
}
```

**Документация поля:**

```java
/** Уникальный идентификатор лабораторной работы. Не может быть null, значение должно быть больше 0 */
private Long id;
```

**Использование тега `{@link}`:**

```java
/**
 * Обеспечивает основные операции CRUD над коллекцией типа {@link Hashtable},
 * где ключом является {@link Integer}, а значением — {@link LabWork}.
 */
```

### Особенности автоматического документирования

1. **Первое предложение** javadoc-комментария автоматически становится кратким описанием (summary) в сводных таблицах.
2. **Наследование документации** — если метод переопределяет суперкласс/интерфейс и не имеет своего javadoc, документация наследуется. Можно использовать `{@inheritDoc}` для явного наследования.
3. **HTML-теги** допускаются: `<p>`, `<ul>`, `<li>`, `<code>`, `<pre>` и другие.
4. **Обязательные поля** — хорошая практика: каждый `public`/`protected` элемент должен иметь javadoc с `@param` для всех параметров, `@return` для не-void методов и `@throws` для проверяемых исключений.
5. **Генерация с Maven:**

```bash
mvn javadoc:javadoc
```

---
