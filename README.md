# ing-sw-2021-mercurio-niantcho-tosini

**Final Examination of Software Engineering** 

**Politecnico di Milano**

**A.Y. 2020-2021**

**Professor:** Pierluigi San Pietro

**Group:** PSP47

**Students**:
- Alessandro Mercurio (907992)
- Francesco Tosini (916715)
- Patrick Orelien Niantcho Njanda (914440)

## Subject

Aim of this project is to design, develop, test and deploy a distributed application: a digital version of the board game "Master of Renaissance" by **Cranio Creations***.

See [Requirements](deliverables/requirements.pdf) to read the full project specifications and requirements (in Italian).

***DISCLAIMER**: WE DO NOT OWN ANY OF THE RESOURCES USED TO REPRODUCE THIS GAME. Every material and graphic resource belong to Cranio Creations Srl (http://www.craniocreations.it/).
What we do own is the code which we wrote the software in.

## Implemented features

| Feature | Implemented |
| ------- | ----------- |
| Complete rules | :heavy_check_mark: |
| CLI | :heavy_check_mark: |
| GUI | :heavy_check_mark: |
| Socket | :heavy_check_mark: |
| Advanced functionality 1 (FA 1) | :heavy_check_mark: Multiple matches ("Partite multiple") |
| Advanced functionality 2 (FA 2) | :heavy_check_mark: Disconnection resilience ("Resilienza alle disconnessioni") |
| Advanced functionality 3 (FA 3) | :heavy_check_mark: Offline match ("Partita locale") |

## Testing

Extensive testing has been performed on all parts of the software, both by writing unit tests and with manual QA.
The unit tests cover 100% of the classes and 89% of the lines of the model on the server. The few non-covered lines mainly deal with runtime exceptions.
The client has less unit tests since most of it deals with UI-specific functions that would have need to be mocked, thus resulting in testing the mocks more than the client itself.

## Compile

To run the tests and compile the software:

1. Install [Java SE 15](https://docs.oracle.com/en/java/javase/15/)
2. Install [Maven](https://maven.apache.org/install.html)
3. Clone this repository
4. In the cloned repo folder, run:
```bash
mvn package
```

