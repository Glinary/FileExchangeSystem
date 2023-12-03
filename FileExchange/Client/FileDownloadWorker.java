package Client;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.SwingWorker;

public class FileDownloadWorker extends SwingWorker<Void, Void> {
        private final String filename;
        private final long fileSize;

        private DataInputStream dataInputStream;

        public FileDownloadWorker(String filename, long fileSize) {
            this.filename = filename;
            this.fileSize = fileSize;
        }

        @Override
        protected Void doInBackground() throws Exception {
            try (FileOutputStream fileOutputStream = new FileOutputStream(filename);
                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {

                    byte[] buffer = new byte[1024];
                        int bytesRead;
                        long receivedData = 0;
    
                        // Receive the file content in chunks
                        while (receivedData < fileSize) {
                            bytesRead = dataInputStream.read(buffer);
                            receivedData += bytesRead;
                            bufferedOutputStream.write(buffer, 0, bytesRead);
                        }

                System.out.println("File received: " + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }