This repo displays a bug in KTOR 3.0.0-beta2 causing requests using Gzip to not complete if the 
ContentEncoding is configured. In the base version the request to Wikipedia will not complete. To make
the request complete, comment out the marked line in [KtorBugSample.kt](app%2Fsrc%2Fmain%2Fjava%2Fcom%2Ftest%2Fktor%2FKtorBugSample.kt) or downgrade to KTOR 2.3.12

|  KTOR 3.0.0-beta2  |  KTOR 2.3.12   |
| ------------- | ------------- |
| ![ddd](https://github.com/crysxd/ktor-content-encoding-bug-demo/blob/main/broken.png?raw=true) | ![](https://github.com/crysxd/ktor-content-encoding-bug-demo/blob/main/working.png?raw=true) |
