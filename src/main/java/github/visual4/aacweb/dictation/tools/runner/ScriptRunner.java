package github.visual4.aacweb.dictation.tools.runner;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class ScriptRunner {

    final private Flusher out;
    final private Flusher err;
    public ScriptRunner() {
        out = new Flusher(System.out);
        out.start();
        err = new Flusher(System.err);
        err.start();
    }
    
    public void execute(Consumer<Integer> callback, String ... cmd) {
        ProcessBuilder builder = new ProcessBuilder().command(cmd);
        Integer response = null; 
        try {
            Process proc = builder.start();
            out.flushFrom(proc.getInputStream());
            err.flushFrom(proc.getErrorStream());
            response = proc.waitFor();
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            callback.accept(response);
        }
    }
    
    private static class Flusher extends Thread {
        BufferedInputStream in ;
        final BufferedOutputStream out;
        
        Lock lock = new ReentrantLock();
        Condition waitInput = lock.newCondition();
        
        public Flusher(OutputStream out) {
            this.out = new BufferedOutputStream(out);
        }
        
        void flushFrom(InputStream in) {
            lock.lock();
            try {
                this.in = new BufferedInputStream(in);
                waitInput.signalAll();
            } finally {
                lock.unlock();
            }
        }
        @Override
        public void run() {
            
            while (this.in == null) {
                lock.lock();
                try {
                    if (this.in== null) {
                        waitInput.await();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
            
            int c = 0;
            try {
                while ((c = in.read()) != -1) {
                    out.write(c);
                }
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeStream();
            }
        }
        
        private void closeStream() {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            try {
//                out.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
    
    public static void main(String[] args) {
        String prefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String filename = "aacweb-dump-" + prefix + ".sql";
        new ScriptRunner().execute((exitCode) -> {
            System.out.println("#exit " + exitCode);
        }, "bash", "./db-dump.sh", filename);
    }
}