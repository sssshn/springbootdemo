package com.sss.activiti.d_processVariables;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProcessVariablesTest {

    @Autowired
    private RepositoryService repositoryService;    //与流程定义和部署对象相关的service

    @Autowired
    private RuntimeService runtimeService;  //与正在执行的流程实例和执行对象相关的service

    @Autowired
    private TaskService taskService; //与任务(正在执行)

    @Autowired
    private HistoryService historyService;

    //手动部署流程定义(从InputStream)
    @Test
    public void deploymentProcessDefinition_inputStream() {
        InputStream inputStreambpmn = this.getClass().getResourceAsStream("/process/processVariables.bpmn");
        InputStream inputStreampng = this.getClass().getResourceAsStream("/process/processVariables.png");
        Deployment deploy = repositoryService.createDeployment()//创建一个部署对象
                .name("流程定义")//添加部署的名称
                .addInputStream("processVariables.bpmn", inputStreambpmn)//使用资源文件的名称(要求:与资源文件名称一致)和输入流完成部署
                .addInputStream("processVariables.png", inputStreampng)//使用资源文件的名称(要求:与资源文件名称一致)和输入流完成部署
                .deploy();//完成部署
        System.out.println("流程部署ID： " + deploy.getId());
        System.out.println("流程部署名称： " + deploy.getName());
    }

    //启动流程实例
    @Test
    public void startProcessInstance() {
        //流程定义的key
        String key = "processVariables";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key);//使用流程定义的key启动流程实例，key对应的helloworld文件中的id属性值,使用key启动，默认是按照最新版本的流程定义启动
        System.out.println("流程实例ID： " + processInstance.getId());
        System.out.println("流程定义ID： " + processInstance.getProcessDefinitionId());
    }

    //设置流程变量
    @Test
    public void setVariables() {
        //任务ID
        String taskId = "52505";
        /* 一:设置流程变量,使用基本数据类型 */
//        taskService.setVariableLocal(taskId, "请假天数", 5);    //与任务ID绑定
//        taskService.setVariable(taskId, "请假日期", new Date());
//        taskService.setVariable(taskId, "请假原因", "回家探亲,过年了");

        /* 二:设置流程变量,使用javabean类型 */
        //当一个javabean(实现序列化)放置到流程变量中,要求javabean的属性不能再发生变化,如果发生变化,在获取的时候,会抛出异常
        //解决方案:private static final long serialVersionUID = -5555847275119998767L;
        Person person = new Person();
        person.setId(3);
        person.setName("村花杨超越");
        taskService.setVariable(taskId, "人员信息新", person);
        System.out.println("设置流程变量成功");
    }

    //获取流程变量
    @Test
    public void getVariables() {
        //任务ID
        String taskId = "52505";
        /* 一: 获取流程变量,使用基本数据类型 */
//        Integer days = (Integer) taskService.getVariable(taskId, "请假天数");
//        Date date = (Date) taskService.getVariable(taskId, "请假日期");
//        String resean = (String) taskService.getVariable(taskId, "请假原因");
//        System.out.println("请假天数: "+days);
//        System.out.println("请假日期: "+date);
//        System.out.println("请假原因: "+resean);

        /* 二:获取流程变量,使用javabean类型 */
        Person person = (Person) taskService.getVariable(taskId,"人员信息新");
        System.out.println(person.getName()+"QAQ"+person.getId());

    }

    //模拟设置和获取流程变量的场景
    public void setAndGetVariables() {
        //与流程实例,执行对象(正在执行)

        //设置流程变量
//        runtimeService.setVariable(executionId, variableName, value);//表示使用执行对象ID和流程变量的名称,设置流程变量的值(一次只能设置一个值)
//        runtimeService.setVariables(executionId, variables);//表示使用执行对象ID和Map集合设置流程变量,map集合的key就是流程变量的名称,map集合的value就是流程变量的值(一次设置多个值)

//        taskService.setVariable(taskId, variableName, value);//表示使用任务ID和流程变量的名称,设置流程变量的值(一次只能设置一个值)
//        taskService.setVariables(taskId, variables));//表示使用任务ID和Map集合设置流程变量,map集合的key就是流程变量的名称,map集合的value就是流程变量的值(一次设置多个值)

//        runtimeService.startProcessInstanceByKey(processDefinitionKey, variables);//启动流程实例的同时,可以设置流程变量,用map集合
//        taskService.complete(taskId, variables);//完成任务的同时,设置流程变量,用Map集合

        //获取流程变量
//        runtimeService.getVariable(executionId, variableName);//使用执行对象ID和流程变量的名称,获取流程变量的值
//        runtimeService.getVariables(executionId);//使用执行对象ID,获取所有的流程变量,获取所有的流程变量,将流程变量放到Map集合中,map集合的key就是流程变量的名称,map集合的value就是流程变量的值
//        runtimeService.getVariables(executionId, variableNames);//使用执行对象ID,获取流程变量的值,通过设置流程变量的名称存放到集合中,获取指定流程变量名称的流程变量的值,值存到map集合中

//        taskService.getVariable(taskId, variableName);//使用任务ID和流程变量的名称,获取流程变量的值
//        taskService.getVariables(taskId);//使用任务ID,获取所有的流程变量,获取所有的流程变量,将流程变量放到Map集合中,map集合的key就是流程变量的名称,map集合的value就是流程变量的值
//        taskService.getVariables(taskId, variableNames);//使用任务ID,获取流程变量的值,通过设置流程变量的名称存放到集合中,获取指定流程变量名称的流程变量的值,值存到map集合中

    }

    //完成我的任务
    @Test
    public void completeMyPersonalTask() {
        //任务ID
        String taskId = "60002";
        taskService.complete(taskId);
        System.out.println("完成任务: 任务ID :" + taskId);
    }

    //查询流程变量的历史表
    @Test
    public void findHistoryProcessVariables (){
        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery()//创建一个历史的流程变量查询对象
                .variableName("请假天数")
                .list();
        if (list != null && list.size() > 0) {
            for (HistoricVariableInstance hvi : list) {
                System.out.println(hvi.getId()+"     "+hvi.getProcessInstanceId()+"     "+hvi.getVariableTypeName()+"      "+hvi.getVariableName()+"       "+hvi.getValue());
                System.out.println("###################");
            }
        }
    }
}
