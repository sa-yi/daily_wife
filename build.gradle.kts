plugins {
    id("java")
}

group = "com.sayi"
version = "1.0-SNAPSHOT"

repositories {
    maven { url =uri("https://maven.aliyun.com/repository/public/") }
    mavenCentral()
    flatDir{
        dirs("libs")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    //compileOnly("com.mikuac:shiro:2.3.8")

    compileOnly("org.springframework.boot:spring-boot-starter-websocket:3.4.4")
    compileOnly(files("libs/shiro-2.3.8.jar"))
}

tasks.test {
    useJUnitPlatform()
}