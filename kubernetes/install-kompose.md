## Installing Kompose on Windows

1. Download the latest Windows release of Kompose from the [Kompose releases page](https://github.com/kubernetes/kompose/releases).

2. Extract the downloaded ZIP file to a folder of your choice.

3. Add the folder where you extracted Kompose to your system's PATH environment variable.

    - Open the Start menu and search for "Environment Variables".
    - Select "Edit the system environment variables".
    - Click the "Environment Variables" button.
    - In the "System Variables" section, find the "Path" variable and click "Edit".
    - Click "New" and add the path to the folder where you extracted Kompose.
    - Click "OK" to close all windows and apply the changes.

4. Verify that Kompose is installed correctly by opening a new Command Prompt window and running the following command:

```cmd
kompose version
```

## Installing Kompose on Linux

1. Download the latest Linux release of Kompose from the [Kompose releases page](https://github.com/kubernetes/kompose/releases).

2. Extract the downloaded tarball to a folder of your choice.

```bash
tar -xf kompose-linux-amd64.tar.gz
```

3. Make the binary executable by running the following command:

```bash
chmod +x kompose-linux-amd64
```

4. Move the binary to a directory in your system's PATH by running the following command:

```bash
sudo mv kompose-linux-amd64 /usr/local/bin/kompose
```

5. Verify that Kompose is installed correctly by running the following command:

```bash
kompose version
``` 

If everything was installed correctly, you should see the version number of Kompose printed to the terminal.