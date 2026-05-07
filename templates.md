24 шаблона проектирования

Порождающие

1. Singleton (Одиночка)
Суть: Гарантирует наличие единственного экземпляра класса в системе и предоставляет к нему глобальную точку доступа.
Реализация в коде: Класс содержит приватную статическую переменную для хранения своего экземпляра и приватный конструктор. Публичный статический метод (например, getInstance()) инициализирует этот экземпляр при первом вызове (ленивая инициализация) и возвращает его при последующих обращениях.
Пример из кода:
```java
public class PasswordEncryptor {

	private static PasswordEncryptor instance;
	private final HashStrategy strategy;

	private PasswordEncryptor() {
		this.strategy = new Sha224HashStrategy();
	}

	public static synchronized PasswordEncryptor getInstance() {
		if (instance == null) instance = new PasswordEncryptor();
		return instance;
	}
}
```
```java
public class DatabaseFacade {
	private static DatabaseFacade instance;
	private final PostgresDao dao;

	private DatabaseFacade(){
		dao = new PostgresDao();
	}

	public static synchronized DatabaseFacade getInstance(){
		if (instance == null) instance = new DatabaseFacade();
		return instance;
	}
}
```

2. Object Pool (Пул объектов)
Суть: Применяется, когда создание объектов требует больших вычислительных ресурсов. Позволяет не создавать их заново, а переиспользовать уже существующие.
Реализация в коде: Специальный класс Pool хранит коллекцию инициализированных объектов. Клиент вызывает метод acquire() для получения готового объекта, а по завершении работы возвращает его обратно в пул с помощью метода release().
Пример из кода (пул соединений HikariCP):
```java
HikariConfig config = new HikariConfig();
config.setJdbcUrl(POSTGRES_URL);
config.setUsername(POSTGRES_USER);
config.setPassword(POSTGRES_PASSWORD);
config.setMaximumPoolSize(10);

dataSource = new HikariDataSource(config);
```

3. Factory Method (Фабричный метод)
Суть: Задает общий интерфейс для создания продуктов, но делегирует решение о том, какой именно класс инстанцировать, своим подклассам.
Реализация в коде: Абстрактный класс или интерфейс Creator объявляет абстрактный метод factoryMethod(). Конкретные классы-наследники переопределяют этот метод, вызывая конструктор new и возвращая объекты конкретных типов, которые объединены общим интерфейсом Product.
Пример из кода:
```java
public static Command create(String name) {
	return cache.computeIfAbsent(name, CommandFactory::instantiate);
}

private static Command instantiate(String name) {
	Command c;
	switch (name) {
		case "insert": c = new InsertCommand(); break;
		case "update": c = new UpdateCommand(); break;
		case "login": c = new LoginCommand(); break;
		case "reg": c = new RegisterCommand(); break;
		default: c = null; break;
	}
	return c;
}
```

4. Abstract Factory (Абстрактная фабрика)
Суть: Предоставляет интерфейс для создания целого семейства взаимосвязанных продуктов. Это устраняет зависимость клиентского кода от конкретных классов конечных объектов.
Реализация в коде: Объявляется интерфейс AbstractFactory, содержащий методы для генерации продуктов каждого типа из семейства (например, createProductA(), createProductB()). Конкретные фабрики реализуют этот интерфейс, возвращая совместимые друг с другом продукты строго определенного семейства.
Пример из кода: В коде не реализовано.

5. Prototype (Прототип)
Суть: Позволяет создавать новые объекты путем копирования (клонирования) уже существующих объектов-прототипов, что избавляет от зависимости от их классов.
Реализация в коде: Интерфейс Prototype объявляет метод clone(). Конкретные классы реализуют этот метод, выделяя память и возвращая копию самого себя с точным переносом значений внутренних полей (глубокая или поверхностная копия).
Пример из кода: В коде не реализовано.

6. Builder (Строитель)
Суть: Позволяет поэтапно конструировать сложные объекты, инкапсулируя логику их пошаговой сборки.
Реализация в коде: Интерфейс Builder объявляет набор методов для пошаговой инициализации частей объекта (например, buildPartA(), buildPartB()). Управляющий класс Director последовательно вызывает эти методы для задания нужной конфигурации, после чего строитель возвращает финальный продукт методом create().
Пример из кода:
```java
public class LabWorkBuilder {
	private final LabWork lw = new LabWork();

	public LabWorkBuilder setId(Long id){ lw.setId(id); return this; }
	public LabWorkBuilder setName(String name){ lw.setName(name); return this; }
	public LabWorkBuilder setCoordinates(Coordinates c){ lw.setCoordinates(c); return this; }
	public LabWorkBuilder setCreationDate(java.util.Date d){ lw.setCreationDate(d); return this; }
	public LabWorkBuilder setMinimalPoint(Float f){ lw.setMinimalPoint(f); return this; }
	public LabWorkBuilder setMaximumPoint(long m){ lw.setMaximumPoint(m); return this; }
	public LabWorkBuilder setDescription(String desc){ lw.setDescription(desc); return this; }
	public LabWorkBuilder setDifficulty(Difficulty diff){ lw.setDifficulty(diff); return this; }
	public LabWorkBuilder setAuthor(Person p){ lw.setAuthor(p); return this; }

	public LabWork build(){ return lw; }
}
```

Структурные

7. Adapter (Адаптер)
Суть: Адаптирует один интерфейс к другому, позволяя объектам с изначально несовместимыми интерфейсами работать совместно.
Реализация в коде: Класс Adapter реализует целевой интерфейс, который ожидается клиентом, и хранит внутри себя ссылку на инстанс адаптируемого класса. В методах целевого интерфейса адаптер вызывает (делегирует) нужные специфические методы вложенного объекта.
Пример из кода: В коде не реализовано.

8. Bridge (Мост)
Суть: Разделяет абстракцию и ее реализацию на две независимые иерархии, позволяя изменять и развивать их независимо друг от друга.
Реализация в коде: Класс абстракции содержит поле со ссылкой на интерфейс реализации Implementor. Вместо того чтобы содержать собственную жестко зашитую логику, методы абстракции делегируют выполнение работы объекту реализации вызовом его методов (imp.operationImp()).
Пример из кода: В коде не реализовано.

9. Composite (Компоновщик)
Суть: Группирует объекты в древовидные иерархические структуры и позволяет клиентскому коду работать с отдельными элементами и целыми ветвями абсолютно единообразно.
Реализация в коде: Создается единый интерфейс Component для листьев и контейнеров. Класс-контейнер (Composite) хранит список дочерних элементов Component. При вызове метода у контейнера, он в цикле обходит своих потомков и рекурсивно вызывает у каждого из них ту же самую операцию.
Пример из кода: В коде не реализовано.

10. Decorator (Декоратор)
Суть: Динамически добавляет новую функциональность объектам путем их оборачивания, используя делегирование вместо наследования.
Реализация в коде: Класс Decorator реализует тот же интерфейс Component, что и оборачиваемый объект, и сохраняет ссылку на него. В переопределенном методе декоратор вызывает базовый метод вложенного объекта (component.operation()) и выполняет свой дополнительный код до или после этого вызова.
Пример из кода:
```java
public class LoggingCommandDecorator implements Command, AuthCommand, Modable, ObjectModable, DelegatingCommand {
	private final Command delegate;

	public LoggingCommandDecorator(Command delegate) {
		this.delegate = delegate;
	}

	@Override
	public Response execute(CollectionManager collectionManager, String login) {
		logger.info("Executing command: {} by user {}", delegate.describe(), login);
		Response r = delegate.execute(collectionManager, login);
		logger.info("Result: success={}, message={}", r.isSuccess(), r.getMessage());
		return r;
	}
}
```
```java
public class AuthorizedCommandDecorator implements Command, Modable, ObjectModable, DelegatingCommand {
	private final Command delegate;

	public AuthorizedCommandDecorator(Command delegate) {
		this.delegate = delegate;
	}

	@Override
	public Response execute(CollectionManager collectionManager, String login) {
		if (login == null || login.isEmpty()) {
			return new Response("Ошибка: требуется аутентификация. Войдите или зарегистрируйтесь.", false);
		}
		return delegate.execute(collectionManager, login);
	}
}
```

11. Facade (Фасад)
Суть: Предоставляет унифицированный и упрощенный интерфейс для взаимодействия с целой подсистемой сложных компонентов, скрывая ее архитектуру от клиента.
Реализация в коде: Создается отдельный класс Facade. В его публичных методах скрыто инстанцируются объекты различных подсистем и выстраиваются правильные цепочки их вызовов (инициализация, выполнение, закрытие), предоставляя клиенту готовый простой метод.
Пример из кода:
```java
public class DatabaseFacade {
	private static DatabaseFacade instance;
	private final PostgresDao dao;

	private DatabaseFacade(){
		dao = new PostgresDao();
	}

	public static synchronized DatabaseFacade getInstance(){
		if (instance == null) instance = new DatabaseFacade();
		return instance;
	}

	public boolean addLabWork(String userLogin, LabWork labWork, Integer key){
		return dao.addLabWork(userLogin, labWork, key);
	}
}
```

12. Flyweight (Легковес)
Суть: Оптимизирует использование оперативной памяти при работе с гигантским числом мелких объектов за счет разделения состояний.
Реализация в коде: Внутреннее, общее для множества объектов (неизменяемое) состояние выносится в класс Flyweight. Внешнее (уникальное, изменяемое) состояние не хранится внутри объекта, а передается в его методы в виде параметров operation(extrinsicState) во время выполнения.
Пример из кода: В коде не реализовано.

13. Proxy (Заместитель)
Суть: Перехватывает обращения к реальному объекту, осуществляя контроль или подмену вызовов. Может выполнять ленивую инициализацию, кэширование или логирование.
Реализация в коде: Класс Proxy реализует интерфейс реального субъекта и содержит ссылку на него. Внутри метода Proxy может выполнять предварительные условия (например, если объект равен null, создать его real = new RealSubject()), после чего делегирует вызов оригинальному объекту real.request().
Пример из кода: В коде не реализовано.

Поведенческие

14. Chain of Responsibility (Цепочка обязанностей)
Суть: Позволяет передавать запрос последовательно по цепочке связанных между собой потенциальных обработчиков. Снижает зависимость между клиентом и обработчиками.
Реализация в коде: Создается базовый класс Handler, содержащий ссылку на следующий в цепочке обработчик (next). В переопределенном методе handleRequest() каждый наследник проверяет условие: если он способен обработать задачу — выполняет ее, если нет — передает вызов по цепочке next.handleRequest().
Пример из кода: В коде не реализовано.

15. Command (Команда)
Суть: Разделяет код, инициирующий выполнение действия, и код, реализующий эту логику. Инкапсулирует вызов в виде самостоятельного объекта.
Реализация в коде: Интерфейс Command объявляет метод execute(). Класс-инициатор (Invoker) сохраняет ссылку на конкретную команду. В нужный момент Invoker вызывает метод execute(), а конкретная реализация команды внутри делегирует вызов к объекту-исполнителю (Receiver).
Пример из кода:
```java
public interface Command extends Serializable {
	public Response execute(CollectionManager collectionManager, String login);
	public String describe();
	public int numberArgsRequired();
}
```
```java
public class CommandInvoker {
	private static final HashMap<String, Command> commands = new HashMap<>();

	public Response executeRequest(Request request, CollectionManager collectionManager) {
		Command command = commands.get(commandName);
		return command.execute(collectionManager, request.getLogin());
	}
}
```

16. Interpreter (Интерпретатор)
Суть: Задает представление грамматики и механизм интерпретации для парсинга и обработки элементов собственного языка или выражений.
Реализация в коде: Программа формирует абстрактное синтаксическое дерево (AST) из классов терминальных и нетерминальных выражений. Каждый такой класс переопределяет метод interpret(context). В нетерминальных классах этот метод рекурсивно вызывает interpret() у вложенных узлов дерева.
Пример из кода: В коде не реализовано.

17. Iterator (Итератор)
Суть: Обеспечивает последовательный доступ ко всем элементам сложной структуры (коллекции), не раскрывая при этом клиентам ее внутреннего устройства.
Реализация в коде: Объявляется интерфейс Iterator с методами обхода: next() для получения элемента и hasNext() для проверки завершения цикла. Коллекция реализует метод createIterator(), который инстанцирует и возвращает клиенту объект итератора, внутри которого ведется учет текущей позиции курсора (например, current++).
Пример из кода: В коде не реализовано.

18. Mediator (Посредник)
Суть: Уменьшает связность классов друг с другом, перенаправляя их общение через центральный объект. Объекты не вызывают методы друг друга напрямую.
Реализация в коде: Объекты (Colleague) содержат только ссылку на абстрактный интерфейс Mediator. При событии они вызывают метод посредника. Реализация класса Mediator хранит ссылки на все компоненты системы и берет на себя логику условных проверок, вызывая методы нужных компонентов.
Пример из кода: В коде не реализовано.

19. Memento (Хранитель)
Суть: Позволяет фиксировать и восстанавливать внутреннее состояние объекта так, чтобы не нарушать его инкапсуляцию.
Реализация в коде: Класс Originator (создатель) предоставляет метод save(), который инстанцирует специальный объект-снимок Memento и записывает в него приватные поля своего состояния. Другой класс-опекун (Caretaker) может хранить снимок и, при необходимости, передать его в метод restore(Memento), где Originator заберет свои значения обратно.
Пример из кода: В коде не реализовано.

20. Observer (Наблюдатель)
Суть: Задает механизм динамической подписки. Когда состояние одного объекта меняется, он асинхронно оповещает об этом множество зависимых объектов.
Реализация в коде: Класс-издатель предоставляет методы attach(o)/detach(o) для управления списком объектов-подписчиков. При изменении своего состояния издатель в цикле вызывает метод update() у каждого подписчика из списка, передавая им новые данные.
Пример из кода: В коде не реализовано.

21. State (Состояние)
Суть: Меняет поведение объекта в зависимости от текущего внутреннего состояния (реализация конечного автомата), избавляя код от громоздких if/else.
Реализация в коде: Создается интерфейс State. Каждое состояние реализуется в виде отдельного класса со своей логикой методов. Класс-контекст хранит текущий экземпляр State и делегирует ему работу. Классы-состояния в своих методах могут возвращать новые объекты (например, return new Red()), переключая тем самым контекст на следующее состояние.
Пример из кода: В коде не реализовано.

22. Strategy (Стратегия)
Суть: Инкапсулирует семейство алгоритмов в отдельные классы, позволяя динамически выбирать и заменять их поведение во время выполнения программы.
Реализация в коде: Задается общий интерфейс Strategy. Контекстный класс принимает объект с конкретной реализацией стратегии (например, через сеттер) и делегирует выполнение операции этому объекту (вызов st.findRoute()), что позволяет переключать логику без модификации класса контекста.
Пример из кода:
```java
public interface HashStrategy {
	String hash(String input);
}
```
```java
public class Sha224HashStrategy implements HashStrategy {
	private final MessageDigest md;

	public Sha224HashStrategy() {
		try {
			md = MessageDigest.getInstance("SHA-224");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-224 not available", e);
		}
	}

	@Override
	public synchronized String hash(String input) {
		byte[] dig = md.digest(input.getBytes(StandardCharsets.UTF_8));
		StringBuilder sb = new StringBuilder(dig.length * 2);
		for (byte b : dig) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) sb.append('0');
			sb.append(hex);
		}
		return sb.toString();
	}
}
```
```java
public class PasswordEncryptor {
	private static PasswordEncryptor instance;
	private final HashStrategy strategy;

	private PasswordEncryptor() {
		this.strategy = new Sha224HashStrategy();
	}

	public String getPasswordHash(String password) {
		return strategy.hash(password);
	}
}
```

23. Template Method (Шаблонный метод)
Суть: Задает жесткий каркас (последовательность шагов) алгоритма в базовом классе, при этом делегируя реализацию специфического поведения отдельных шагов подклассам.
Реализация в коде: В абстрактном базовом классе создается публичный метод, содержащий фиксированную последовательность вызовов внутренних методов (например, calculateDamage(), applySpecialEffects()). Классы-наследники переопределяют пустые ("хуки") или абстрактные методы (например, applySpecialEffects()), изменяя детали работы, но сохраняя порядок из базового класса.
Пример из кода: В коде не реализовано.

24. Visitor (Посетитель)
Суть: Отделяет логику выполняемых операций от самой структуры классов элементов. Применяется, если структура стабильна, но требуется добавление новых операций.
Реализация в коде: Описывается интерфейс Visitor с методами вида visit(Element e). Внутри классов элементов реализуется метод accept(Visitor v), который вызывает соответствующий метод пришедшего посетителя: v.visit(this) (механизм двойной диспетчеризации).
Пример из кода: В коде не реализовано.
