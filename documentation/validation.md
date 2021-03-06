#Validation Support
The Dolphin Platform provides the optional ``` dolphin-platform-bean-validation ``` module that adds Java Bean Validation (JSR 303) support to the model layer of the Dolphin Platform. To use the validation support the module must be added as a dependency to your project:
```xml
<dependency>
    <groupId>com.canoo.dolphin-platform</groupId>
    <artifactId>dolphin-platform-bean-validation</artifactId>
    <version>DOLPHIN_PLATFORM_VERSION</version>
</dependency>
```
If you have defined a common module that contains the model descriptions and is shared between client and server you can simply replace the ``` dolphin-platform-core ``` dependency with ``` dolphin-platform-bean-validation ``` since the validation module adds a transitive dependency to the core module.
![Dependencies for the validation module](validation-dependency.png)
The ``` dolphin-platform-bean-validation ``` module don't depend on a JSR-303 implementation. If your application server don't provide an implementation you need to add for example Hibernate Validation as a dependency. Here is an example for Maven:
```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>5.1.3.Final</version>
</dependency>
<dependency>
    <groupId>org.glassfish</groupId>
    <artifactId>javax.el</artifactId>
    <version>3.0.0</version>
</dependency>
```
If you depend on JavaEE your application container will automatically provide an implementation for you. In that case you don't need to add a specific implementation as a dependency.
### Using validation contraints
Once the module is added as a dependency the contrains annotations of JSR-303 can be used in Dolphin Platform models. To do so the ``` Property ``` fields of a model bean must be annotated with the contrains annotations. Here is an example that defines a "not null" constrain for a ``` String ``` property:
```Java
@DolphinBean
public class MyModel {

    @NotNull
    private Property<String> value;

    public Property<String> valueProperty() {
        return value1;
    }
}

```
A general description of the Dolphin Platform model API can be found here.
### Validate a model
By using a validator you can now easily validate instances of the model as described in the bean validation documentation or several tutorials. Here is a basic code snippet that creates a validator by hand and validates a Dolphin Platform model:
```Java
Configuration<?> validationConf = Validation.byDefaultProvider().configure();

Validator validator = validationConf.buildValidatorFactory().getValidator();

Set<ConstraintViolation<TestBean>> violations = validator.validate(dolphinModel);
if(!violations.isEmpty()) {
    //Handle violations
}

```
Some platforms provide injection of a ``` Validator ``` instance. In that case you don't need to create a configuration and validator by hand.
