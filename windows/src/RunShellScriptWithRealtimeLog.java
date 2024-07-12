import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunShellScriptWithRealtimeLog {
    public static void main(String[] args) {
        // 获取当前JAR文件的运行目录
        String currentDir = System.getProperty("user.dir");
        
        // 脚本文件路径
        String scriptPath = currentDir + File.separator + "start.bat";
        
        // 创建ProcessBuilder对象，并设置脚本路径
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", scriptPath);
        
        // 设置工作目录为当前目录
        processBuilder.directory(new File(currentDir));
        
        Process process = null;
      
        try {
            // 启动进程
            Process process = processBuilder.start();
            
            // 创建线程来读取标准输出流
            Thread outputThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            
            // 创建线程来读取标准错误流
            Thread errorThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.err.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // 启动线程
            outputThread.start();
            errorThread.start();

            // 等待脚本执行完成
            int exitCode = process.waitFor();

            // 确保输出线程和错误线程已完成
            outputThread.join();
            errorThread.join();

            // 输出脚本执行结果
            System.out.println("Script executed with exit code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
