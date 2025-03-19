package com.mxs.ojproject.judge.codesandbox;

import com.mxs.ojproject.judge.codesandbox.impl.ExampleCodeSandbox;
import com.mxs.ojproject.judge.codesandbox.impl.RemoteCodeSandbox;
import com.mxs.ojproject.judge.codesandbox.impl.ThirdPartyCodeSandbox;
import com.mxs.ojproject.judge.codesandbox.model.ExecuteCodeRequest;
import com.mxs.ojproject.judge.codesandbox.model.ExecuteCodeResponse;
import com.mxs.ojproject.model.enums.QuestionSubmitLanguageEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class CodeSandboxTest {

    @Value("${codeSandbox.type:remote}")
    private String typeValue;

    @Autowired
    @Qualifier("exampleCodeSandbox")
    CodeSandbox exampleCodeSandbox;

    @Autowired
    @Qualifier("remoteCodeSandbox")
    CodeSandbox remoteCodeSandbox;

    @Autowired
    @Qualifier("thirdPartyCodeSandbox")
    CodeSandbox thirdPartyCodeSandbox;

    @Test
    void executeCode() {
        String type = "remote";
        String code = "public class Main\n" +
                "{\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        int a = Integer.parseInt(args[0]);\n" +
                "        int b = Integer.parseInt(args[1]);\n" +
                "        System.out.println(\"Result: \" + (a + b));\n" +
                "    }\n" +
                "}";
        String language = QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        ExecuteCodeResponse response = codeSandbox.executeCode(request);
        //Assertions.assertNotNull(response);
        System.out.println(response);
    }

/*    @Test
    void CodeSandboxFactory(String type) {
        String code = "int main() { }";
        String language = QuestionSubmitLanguageEnum.JAVA.getValue();
        List<String> inputList = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest request = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();

        switch(type){
            case "example":
                ExecuteCodeResponse response = exampleCodeSandbox.executeCode(request);
                System.out.println(response);

            case "remote":
                ExecuteCodeResponse response2 = remoteCodeSandbox.executeCode(request);
                System.out.println(response2);

            case "thirdParty":
                ExecuteCodeResponse response3 = thirdPartyCodeSandbox.executeCode(request);
                System.out.println(response3);

            default:
                ExecuteCodeResponse response4 = exampleCodeSandbox.executeCode(request);
                System.out.println(response4);
        }
    }*/

    @Test
    void testFactory(){
        String type = typeValue;
        System.out.println(type);

    }

}