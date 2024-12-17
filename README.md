<p align="left">
  <img src="https://github.com/bluegroundltd/fixtures/blob/master/images/logo.png" />
</p>

# Fixtures [![CI](https://github.com/bluegroundltd/fixtures/actions/workflows/ci_test.yaml/badge.svg?branch=master)](https://github.com/bluegroundltd/fixtures/actions/workflows/ci_test.yaml)

`Fixtures` is a library that helps us to instantiate data classes for our tests easily. We were inspired by this [blog post](https://phauer.com/2018/best-practices-unit-testing-kotlin/#use-helper-functions-with-default-arguments-to-ease-object-creation),
and decided to use [KSP](https://kotlinlang.org/docs/ksp-overview.html) to automate the generation of the described functions.

## Installation
Before using this library, we must set up KSP in our project. We can follow the instructions [here](https://kotlinlang.org/docs/ksp-quickstart.html#use-your-own-processor-in-a-project). Then we must include the following dependency:

``` 
implementation("io.github.bluegroundltd:fixtures-annotations:1.0.7")
implementation("io.github.bluegroundltd:fixtures:1.0.7")
```

## How to
All we have to do is to add the `Fixture` annotation in a data class. For example, if we have the following data class:

```kotlin
@Fixture
data class Foo(
     val stringValue: String,
     val intValue: Int,
)
```

Then the following helper function will be generated:

```kotlin
fun createFoo(
     stringValue: String = "stringValue",
     intValue: Int = 0
) : Foo = Foo(
     stringValue = stringValue,
     intValue = intValue
)
```

The naming convention for the generated function is the class' name with the
`create` prefix. So, from the `Foo` class, the `createFoo()` function will be generated.
This function will be placed in a file with the `FooFixture.kt` name, and the file 
will be placed in the `Foo`'s package under the `build/generated/ksp/kotlin/` path.

You can change the default `create` prefix to some other value, for example to generate `foo()` function instead:

```gradle
ksp.arg("fixtures.prefix", "")
```

## Randomize your data
The generated functions have default values for their parameters. The values are [standard](#Supported-field-types) 
based on the parameter's type. If we want to assign random values then we can use the next KSP option:

```gradle
ksp.arg("fixtures.randomize", "true")
```

After applying the previous option, every time we generate the functions new default values will be assigned. Things to
note here are:
- The default behavior is to not randomize the data.
- The randomization happens in every generated function in the Gradle module. In the future, we may consider randomizing
data per fixture.

## Supported field types
The supported field types are:

| Supported type                                                           | default value                                               | randomized default value                                                         |
|--------------------------------------------------------------------------|-------------------------------------------------------------|----------------------------------------------------------------------------------|
| String                                                                   | A string whose value will be equal to the name of the field | An at most 10 characters long string that contains random alphanumeric character |
| Char                                                                     | 'A'                                                         | A random alphanumeric character                                                  |
| Boolean                                                                  | false                                                       | Random.nextBoolean()                                                             |
| Int                                                                      | 0                                                           | Random.nextInt(20)                                                               |
| Long                                                                     | 0L                                                          | Random.nextLong( 20)                                                             |
| Float                                                                    | 0f                                                          | Random.nextFloat()                                                               |
| Double                                                                   | 0.0                                                         | Random.nextDouble(20.0)                                                          |
| Date                                                                     | Date(0)                                                     | Date()                                                                           |
| TimeZone                                                                 | TimeZone.getTimeZone("UTC")                                 | TimeZone.getTimeZone(TimeZone.getAvailableIDs().random())                        |
| UUID                                                                     | UUID.fromString("00000000-0000-0000-0000-000000000000")     | UUID.randomUUID()                                                                |
| LocalDate                                                                | LocalDate.of(1989, 1, 23)                                   | LocalDate.now()                                                                  |
| LocalTime                                                                | LocalTime.of(0, 0)                                          | LocalTime.now()                                                                  |
| LocalDateTime                                                            | LocalDateTime.of(1989, 1, 23, 0, 0)                         | LocalDateTime.now()                                                              |
| ZonedDateTime                                                            | ZonedDateTime.of(1989,1,23,0,0,0,0, ZoneId.of("UTC"))       | ZonedDateTime.now()                                                              |
| Instant                                                                  | Instant.EPOCH                                               | Instant.now()                                                                    |
| OffsetTime                                                               | OffsetTime.MIN                                              | OffsetTime.now()                                                                 |
| OffsetDateTime                                                           | OffsetDateTime.MIN                                          | OffsetDateTime.now()                                                             |
| ZoneId                                                                   | ZoneId.of("UTC")                                            | ZoneId.of(ZoneId.getAvailableZoneIds().random())                                 |
| BigDecimal                                                               | BigDecimal.ZERO                                             | BigDecimal.valueOf(Random.nextDouble(20.0))                                      |
| BigInteger                                                               | BigInteger.ZERO                                             | BigInteger.valueOf(Random.nextInt(20))                                           |
| Array                                                                    | emptyArray()                                                | emptyArray()                                                                     |
| List                                                                     | emptyList()                                                 | emptyList()                                                                      |
| Set                                                                      | emptySet()                                                  | emptySet()                                                                       |
| Map                                                                      | emptyMap()                                                  | emptyMap()                                                                       |
| Enums                                                                    | The first enum entry                                        | Randomly selected enum entry                                                     |
| Sealed classes (their data subclasses should be annotated with @Fixture) | The first sealed class' entry                               | Randomly selected sealed class' entry                                            |
| Other data classes annotated with @Fixture                               | Invocation to other @Fixture's generate function            | Invocation to other @Fixture's generate function                                 |

## Handle not supported field types

There will be some rare cases where we have a data class that contains another class which does not belong in the 
supported field types. Additionally, this not supported class may be part of another library, so we could not use
the `Fixture` annotation on it. Let's assume that we have the following class:

```kotlin
data class Foo(
     val doubleValue: Double,
     val barValue: Bar
)
```

and that `Bar` class is part of another library. To help the processor generate a `Bar` class we can use the 
`FixuteAdapter` annotation. This annotation can be applied to a top-level function, like that:

```kotlin
@FixtureAdapter
fun barFixtureProvider(): Bar = Bar()
```

Then the processor will use the annotated function to generate the helper function.

```kotlin
fun createFoo(
     doubleValue: Double = 0.0,
     barValue: Bar = barFixtureProvider()
) : Foo = Foo(
     doubleValue = doubleValue,
     barValue = barValue
)
```

Unfortunately, the functions that are annotated with the `FixtureAdapter` annotation must be placed into the main source
sets, till the [source set issue](#Source-sets-Issue) is resolved.

## Source sets Issue
For now, we can not run KSP on the main source sets and generate classes in the test source sets. This seems that will not
be the case after fixing this [issue](https://github.com/google/ksp/issues/962). Until then, we can avoid generating 
test functions in our release code by setting the following KSP option:

```gradle
// Instead of setting statically the value to false you 
// should create a function to calculate this value.
ksp.arg("fixtures.run", "false") 
```

The default value of this option is true. We need to explicitly set it to `false` when we are about to run our tests. 
This can be easily done with a function in our Gradle file.

## Multi-module support
#### Fixtures in different modules
If our data class contains a Fixture field, and the declaration of the field's class belongs to another module, then our 
processor can not recognize that it is a Fixture field. The reason is that KSP seems to not provide the annotations of a
class defined in another module. To help our processor we added need to use the `ModularizedFixture` annotation.

For example, if we have defined in one module the following class
```kotlin
data class Foo(val barValue: Bar)
```

and in another module this class
```kotlin
@Fixture
data class Bar(val stringValue: String)
```

then to help our processor to generate the fixture, we need to annotate the field with the `ModularizedFixture` annotation
```kotlin
@Fixture
data class Foo(@ModularizedFixture val barValue: Bar)
```

#### Sealed classes in different modules
If our data class contains a sealed class field, and the declaration of the sealed class belongs to another 
module, then our processor can not recognize that this field is a sealed class. This means that it can not treat it 
accordingly. The reason behind that is that the generated bytecode does not contain any information about being a sealed
class (due to java interoperability). To overcome this issue we can use `FixtureAdapter` as described in
[this section](#Handle-not-supported-field-types).
