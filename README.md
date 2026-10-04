# uni-waikato-software-labs

Coursework archive for University of Waikato software/computer systems labs and assignments.

> Updated for repository contents as of 2026-10-04.

## Project overview

This repository is a **multi-course lab portfolio**, not a single deployable application. It contains source code, reports, datasets, compiled artifacts, and assignment hand-ins across multiple COMPX papers (for example 203, 204, 223, 224, 304, 341, 361, 370, 518, 553, and year-1 exercises).

## Current features

- Java networking labs (socket clients/servers, HTTP server, TFTP protocol implementations, TLS examples)
- Software engineering assignment implementations and tests (COMPX341 ESGP systems)
- Parallel/distributed coursework (OpenCL/JOCL, Hadoop/Spark assignment folders)
- WRAMP/C and assembly exercises with `Makefile`-based builds
- Security and systems lab material (including side-channel and cryptography-related exercises)
- Retinal and image-processing coursework using OpenCV Java code
- Assignment reports, spreadsheets, diagrams, and supporting documents

## Technology stack

Languages and formats currently present in the repository include:

- Java
- C
- Assembly (`.s`)
- Haskell (`.hs`)
- OpenCL kernel code (`.cl`)
- Batch/shell scripts
- Documentation/data assets (`.pdf`, `.txt`, `.xlsx`, `.ods`, images)

Tooling seen in the repo includes:

- `javac` / `java`
- JUnit 5 (for several Java test classes)
- Ant (`553-files/assignment-two-throwing-darts-2023/pi_dart/build.xml`)
- `make` for WRAMP exercises (`compx203/**/Makefile`, `compx223/COMP223-Kernel-main/Makefile`)
- GraphStream JARs in COMPX341 folders
- JOCL JARs in COMPX553 assignment folders

## Repository structure

Top-level layout (selected):

- `/204` - COMPX204 networking, TLS, and chat/TFTP labs
- `/304` - networking, HTTP server, routing, and side-channel labs
- `/307` - Haskell parsing coursework
- `/341` - COMPX341 planning docs + software implementation/testing folders
- `/361` - retinal/OpenCV and photo tool coursework
- `/370` - environmental/lifecycle analysis materials
- `/518` - security/hash-collision related lab material
- `/553-files` - COMPX553 assignments (including OpenCL dart-throwing project)
- `/compx203`, `/compx223`, `/Compx224-Ass4` - WRAMP/C/assembly coursework
- `/YEAR1` - introductory Java exercises

## Prerequisites

Because this is a mixed-course archive, prerequisites depend on the folder you work in. Common requirements:

- Git
- JDK (for Java-based exercises)
- `make` + WRAMP toolchain (`wasm`, `wcc`, `wlink`) for WRAMP projects
- Ant for `pi_dart`
- OpenCL runtime/GPU drivers for JOCL/OpenCL exercises
- OpenCV Java native libraries for retinal/image tasks

## Build and run

There is no single root build command. Use folder-specific commands.

### Example: COMPX341 implementation

From `/341/compx341-implementation`:

```bash
javac -cp ".:gs-core-1.3.jar:gs-ui-1.3.jar:junit-platform-console-standalone-1.8.2.jar" *.java
java -cp ".:gs-core-1.3.jar:gs-ui-1.3.jar" ConsoleApp
```

### Example: COMPX553 OpenCL darts assignment

From `/553-files/assignment-two-throwing-darts-2023/pi_dart`:

```bash
ant run
```

### Example: WRAMP kernel coursework

From `/compx203/WRAMP-Ass5` (or similar WRAMP folders):

```bash
make
make clean
```

## Usage examples

Examples from current source files:

- HTTP server (COMPX304): run `HttpServer` (default port `51235`)
- TFTP client (COMPX304 networking):
  ```text
  TftpClient <name> <port> <file>
  ```
- Photo tool (COMPX361):
  ```text
  java nz.ac.waikato.phototool.PhotoTool grayscale save <image-file>
  ```

## Testing

Testing is project-specific and mostly Java/JUnit-based in selected folders.

### COMPX341 sample tests

From `/341/compx341-implementation` (using bundled JUnit console launcher):

```bash
java -jar junit-platform-console-standalone-1.8.2.jar -cp ".;gs-core-1.3.jar;gs-ui-1.3.jar" -c AccountLoginTests
java -jar junit-platform-console-standalone-1.8.2.jar -cp ".;gs-core-1.3.jar;gs-ui-1.3.jar" -c BuildingGraphTests
```

Additional test classes also exist in networking folders (for example `TftpPacketTest.java`, `HttpServerRequestTest.java`).

## Configuration details

Configuration is mostly hardcoded or file-based per assignment. Examples:

- Network ports are set in source for some labs (e.g. HTTP/chat server defaults to `51235`)
- TFTP client timeout is set in code (`SO_TIMEOUT = 6000`)
- COMPX341 console apps use local text datasets and user files in their directories
- OpenCL assignment parameters are passed as CLI args (`threads workgroupsize repeats`)

## Limitations / current status

- This is an educational archive with heterogeneous quality and project maturity.
- Many folders include compiled binaries, generated outputs, and historical submission snapshots.
- Build/test instructions are not standardized across the whole repository.
- Some scripts and commands are platform-specific (mixed Windows/Linux classpath styles are present).

## License and attribution

No top-level license file is currently present in this repository.

Where needed, refer to assignment-specific documentation and source headers for attribution and usage constraints.
