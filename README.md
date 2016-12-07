# numberreader
Recognize human written numbers

Still a work of progress.
numberreader consists of a jetty server that trains a model to recognize English numbers. Plain SGD is used for training.
Training begins as soon as the server is started. Since it is a time consuming step, a .nn file is generated everytime the server is started.
This .nn file can be used in future runs. It is basically a serialized java object of the neural network. To use a .nn file, update READ_NN_FILE=true in properties file (disk1/characterreader/properties/characterreader.properties)

The program expects the above mentioned properties file. A sample properties file is part of the project itself and can be copied as it is.

The program also expects MNIST data images in jpeg format. This you can find on the internet and extract them out.

A UI is provided where you can draw the numbers on canvas and test the result. The efficiency of this tool is right now not so good as I am building it from scratch. Will keep updating this in future.
