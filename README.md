Udarbejdet af Joachim Nielsen.

HUSK AT ÅBNE I KODE OG IKKE PREVIEW.

Pizza afleverings opgave. Vil gennemgå vores skriftlige analyse nedenunder.

En kort indledning:
Programmet kan ikke køre, jeg har forsøgt at debugge til sidst med Ai, og den konkluderet jeg ikke har initialiseret min JDBC driver korrekt. HTML filerne har jeg ikke selv udarbejdet, men alt koden (85-90%), har jeg selv skrevet.
Har ikke haft tid til at kommentere det, da vi var på kort deadline. 

Til at starte med, kan vi gennemgå vores tegning af lagene.

**Presentation Layer**
- UserController
- PizzaController
- OrderController

Præsentations laget har som det lyder, ansvaret for at præsentere information til brugeren og håndtere brugerinteraktioner. Den modtager og validere brugerinput og implementere brugergrænsefladen via thymeleaf
Derudover håndtere den også GTPP requests og respons.

**Application Layer**
- UserService
- PizzaService
- OrderService

Også det vi kalder for use-case laget. Det er det man kalder en mellemanden for domænelaget og brugeren. Den implementere "forretningslogik".
Ved ikke helt hvordan jeg skal uddybe, men en vigtig detajle er at den er uafhængig af præsentationslaget og databaselaget

**Domain Layer**
- User
- Pizza
- Topping
- Order

Dette lag indeholder systemets kerneobjekter og regler. Det kan man godt kalde for mitekondria i cellen eller hjertet af applikation, har jeg forstået det korrekte. Det står uafhængigt af de ydre lag.

**Infrastructure layer**
- UserRepositoy
- PizzaRepository
- OrderRepository
- DatabaseConfig

Dette er database laget i systemet. Det står for at lagre data, konfigure databaseforbindelser og isolere meget af databaseteknologien fra resten af applikation


**UML Klassediagram.**
Nedenfor vil du se et klassediagram der er udarbjdet af ChatGPT, men med alle mine egne inputs. Havde bare ikke overskuet eller talentet til at formatere det til en readme.

+------------------------------------------------------------------+
|                    PIZZA PARADISE - KLASSEDIAGRAM                |
+------------------------------------------------------------------+

+------------------------+       +------------------------+
|        User            |       |        Pizza           |
+------------------------+       +------------------------+
| - id: Long             |       | - id: Long             |
| - name: String         |       | - name: String         |
| - email: String        |       | - description: String  |
| - password: String     |       | - basePrice: double    |
| - address: String      |       | - toppings: List       |
| - bonusPoints: int     |       +------------------------+
| - orderHistory: List   |       | + addTopping()         |
+------------------------+       | + removeTopping()      |
| + addBonusPoints()     |       | + calculatePrice()     |
| + useBonusPoints()     |       +------------------------+
| + addOrder()           |                 |
+------------------------+                 |
           ^                               |
           |                               |
           | 1                        0..* |
+------------------------+                 |
|        Order           |<----------------+
+------------------------+
| - id: Long             |
| - user: User           |       +------------------------+
| - pizzas: List         |       |       Topping         |
| - orderDate: DateTime  |       +------------------------+
| - totalPrice: double   |       | - id: Long             |
| - isCompleted: boolean |       | - name: String         |
| - earnedBonusPoints: int|<----->| - price: double       |
+------------------------+  0..*  +------------------------+
| + addPizza()           |
| + removePizza()        |
| + calculateTotalPrice()|
| + calculateBonusPoints()|
| + completeOrder()      |
+------------------------+

//==== SERVICE LAYER ====//

+------------------------+       +------------------------+       +------------------------+
|     IUserService       |       |    IPizzaService      |       |    IOrderService      |
+------------------------+       +------------------------+       +------------------------+
| + createUser()         |       | + getAllPizzas()      |       | + createOrder()       |
| + login()              |       | + getAllToppings()    |       | + addPizzaToOrder()   |
| + getUserOrderHistory()|       | + createCustomPizza() |       | + removePizzaFromOrder()|
| + addBonusPoints()     |       | + addToppingToPizza() |       | + completeOrder()     |
| + useBonusPoints()     |       | + removeToppingFromPizza()|   | + getOrdersByUser()   |
| + getUserById()        |       | + getPizzaById()      |       | + getOrderById()      |
+------------------------+       +------------------------+       +------------------------+
           ^                              ^                               ^
           |                              |                               |
           |                              |                               |
+------------------------+       +------------------------+       +------------------------+
|     UserService        |       |     PizzaService      |       |     OrderService      |
+------------------------+       +------------------------+       +------------------------+
| - userRepository       |       | - pizzaRepository     |       | - orderRepository     |
| - orderRepository      |       | - toppingRepository   |       | - userRepository      |
+------------------------+       +------------------------+       | - pizzaRepository     |
                                                                 +------------------------+

//==== REPOSITORY LAYER ====//

+------------------------+       +------------------------+       +------------------------+
|   IUserRepository      |       |   IPizzaRepository    |       |  IToppingRepository   |
+------------------------+       +------------------------+       +------------------------+
| + save()               |       | + save()              |       | + save()              |
| + findById()           |       | + findById()          |       | + findById()          |
| + findByEmail()        |       | + findAll()           |       | + findAll()           |
| + findAll()            |       | + deleteById()        |       | + deleteById()        |
| + deleteById()         |       +------------------------+       +------------------------+
+------------------------+                ^                               ^
           ^                              |                               |
           |                              |                               |
+------------------------+       +------------------------+       +------------------------+
|    UserRepository      |       |    PizzaRepository    |       |   ToppingRepository   |
+------------------------+       +------------------------+       +------------------------+
| - databaseConfig       |       | - databaseConfig      |       | - databaseConfig      |
+------------------------+       | - toppingRepository   |       +------------------------+
                                 +------------------------+

+------------------------+       +------------------------+
|   IOrderRepository     |       |    DatabaseConfig     |
+------------------------+       +------------------------+
| + save()               |       | - dataSource          |
| + findById()           |       +------------------------+
| + findByUser()         |       | + getConnection()     |
| + findAll()            |       +------------------------+
| + deleteById()         |
+------------------------+
           ^
           |
           |
+------------------------+
|    OrderRepository     |
+------------------------+
| - databaseConfig       |
| - userRepository       |
| - pizzaRepository      |
+------------------------+

//==== CONTROLLER LAYER ====//

+------------------------+       +------------------------+       +------------------------+
|    UserController      |       |    PizzaController    |       |    OrderController    |
+------------------------+       +------------------------+       +------------------------+
| - userService          |       | - pizzaService        |       | - orderService        |
+------------------------+       +------------------------+       | - userService         |
| + showLoginForm()      |       | + showMenu()          |       | - pizzaService        |
| + processLogin()       |       | + showCustomPizzaForm()|      +------------------------+
| + showRegistrationForm()|      | + processCustomPizza()|       | + createNewOrder()    |
| + processRegistration()|       | + showPizzaDetails()  |       | + addPizzaToOrder()   |
| + showUserProfile()    |       +------------------------+       | + removePizzaFromOrder()|
| + logout()             |                                        | + viewCurrentOrder()  |
+------------------------+                                        | + showCheckoutForm()  |
                                                                 | + completeOrder()     |
                                                                 | + showOrderHistory()  |
                                                                 | + showOrderDetails()  |
                                                                 +------------------------+


**Klassernes ansvar**
Føler ikke det er noget der skal gennemgås i dybden. Jeg har gennemgået nogenlunde ovenstående i opgave 1, hvordan og hvorledes opbygningen af programmet er.

**Refleksion**
Ingen grund til at gå ind i ansvarsfordeling her.
En kort selvrefleksion: Jeg har benyttet interfaces, ikke fordi jeg har 100% styr på hvad de gør, men fordi jeg havde behov for at prøve det af.
