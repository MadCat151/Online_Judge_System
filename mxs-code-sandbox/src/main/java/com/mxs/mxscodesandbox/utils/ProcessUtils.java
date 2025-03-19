package com.mxs.mxscodesandbox.utils;


import com.mxs.mxscodesandbox.model.ProcessExecuteMsg;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProcessUtils {

    public static ProcessExecuteMsg doProcessMsg(Process runProcess, String opration) {
        ProcessExecuteMsg processExecuteMsg = new ProcessExecuteMsg();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            int exitValue = runProcess.waitFor();
            processExecuteMsg.setExitValue(exitValue);
            if (exitValue == 0) {
                System.out.println(opration + "SUCCESS");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                List<String> outputStrList = new ArrayList<>();
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    outputStrList.add(compileOutputLine);
                }
                //outputStrList.replaceAll(line -> line.replace("\n", ""));
                processExecuteMsg.setMessage(StringUtils.join(outputStrList,""));
                System.out.println("processExecuteMsg: "+processExecuteMsg);
            } else {
                System.out.println(opration + "FAILED" + exitValue);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                List<String> outputStrList = new ArrayList<>();
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    outputStrList.add(compileOutputLine);
                }
                processExecuteMsg.setMessage(StringUtils.join(outputStrList,"\n"));
                //error message
                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
                List<String> errorOutputStrList = new ArrayList<>();
                String errorCompileOutputLine;
                while ((errorCompileOutputLine = errorBufferedReader.readLine()) != null) {
                    errorOutputStrList.add(errorCompileOutputLine);
                }
                processExecuteMsg.setErrorMessage(StringUtils.join(errorOutputStrList,"\n"));
                System.out.println("processExecuteMsg: "+processExecuteMsg);
            }

            stopWatch.stop();
            processExecuteMsg.setTime(stopWatch.getTotalTimeMillis());

        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
        return processExecuteMsg;
    }

}
