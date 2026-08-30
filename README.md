# mock

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./gradlew build
```

It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory. Be aware that it’s not an _über-jar_ as the dependencies are
copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

## Running with Docker

Build the image from the project root (replace `YOUR_DOCKERHUB_USERNAME` with
your Docker Hub username):

```shell script
docker build -t YOUR_DOCKERHUB_USERNAME/sigo-mock:1.0.0 .
```

Run it locally:

```shell script
docker run --rm -p 34000:34000 YOUR_DOCKERHUB_USERNAME/sigo-mock:1.0.0
```

### Configure mock endpoints at runtime

Use one JSON configuration file to define the HTTP method, endpoint path,
response status, and JSON response body. The supported methods are `GET`,
`POST`, `PUT`, `PATCH`, and `DELETE`; paths can contain multiple segments.

```json
{
  "endpoints": [
    {
      "method": "POST",
      "path": "/tickets",
      "status": 201,
      "response": {
        "id": 123,
        "created": true
      }
    }
  ]
}
```

Mount the file and set `MOCK_CONFIG_FILE` to its path inside the container.
The configuration is read on every request, so edits take effect without an
image rebuild or container restart:

```powershell
docker run --rm -p 34000:34000 `
  -e MOCK_CONFIG_FILE=/mock-config/mock-endpoints.json `
  -v "${PWD}/responses:/mock-config:ro" `
  tiagonora/mock:1.0.0
```

With the example file in `responses/mock-endpoints.json`, test it with:

```powershell
curl.exe -i -X POST http://localhost:34000/tickets
curl.exe -i http://localhost:34000/errors/400
curl.exe -i http://localhost:34000/errors/500
```


Publish it to Docker Hub:

```shell script
docker login
docker push YOUR_DOCKERHUB_USERNAME/sigo-mock:1.0.0
```

Optionally also publish the moving `latest` tag:

```shell script
docker tag YOUR_DOCKERHUB_USERNAME/sigo-mock:1.0.0 YOUR_DOCKERHUB_USERNAME/sigo-mock:latest
docker push YOUR_DOCKERHUB_USERNAME/sigo-mock:latest
```

If you want to build an _über-jar_, execute the following command:

```shell script
./gradlew build -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./gradlew build -Dquarkus.native.enabled=true
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/mock-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/gradle-tooling>.

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): Build RESTful web services and APIs using Jakarta REST (formerly JAX-RS)
- REST Jackson ([guide](https://quarkus.io/guides/rest#json-serialisation)): Jackson serialization support for Quarkus REST. This extension
  is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
