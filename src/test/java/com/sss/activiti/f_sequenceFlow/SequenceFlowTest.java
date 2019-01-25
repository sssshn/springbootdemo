package com.sss.activiti.f_sequenceFlow;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 *连线
 * 一个流程中,执行对象可以有多个,但是流程定义只能有一个
 * 当流程定义规则只执行一次的时候,那么流程实例就是执行对象
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SequenceFlowTest {

    @Autowired
    private RepositoryService repositoryService;    //与流程定义和部署对象相关的service

    @Autowired
    private RuntimeService runtimeService;  //与正在执行的流程实例和执行对象相关的service

    @Autowired
    private TaskService taskService;    //与正在执行的任务管理相关的service

    @Autowired
    private HistoryService historyService;  //与历史数据(从历史表)相关的service

    //手动部署流程定义(从InputStream)
    @Test
    public void deploymentProcessDefinition_InputStream() {
        InputStream inputStreamBpmn = this.getClass().getResourceAsStream("sequenceFlow.bpmn");
        InputStream inputStreamPng = this.getClass().getResourceAsStream("sequenceFlow.png");
        Deployment deploy = repositoryService.createDeployment()//创建一个部署对象
                .name("连线")//添加部署的名称
                .addInputStream("sequenceFlow.bpmn", inputStreamBpmn)
                .addInputStream("sequenceFlow.png", inputStreamPng)
                .deploy();//完成部署
        System.out.println("流程部署ID： " + deploy.getId());
        System.out.println("流程部署名称： " + deploy.getName());
    }

    //启动流程实例
    @Test
    public void startProcessInstance() {
        //流程定义的key
        String key = "sequenceFlow";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key);//使用流程定义的key启动流程实例，key对应的helloworld文件中的id属性值,使用key启动，默认是按照最新版本的流程定义启动
        System.out.println("流程实例ID： " + processInstance.getId());
        System.out.println("流程定义ID： " + processInstance.getProcessDefinitionId());
    }

    //查询当前人的个人任务
    @Test
    public void findMyPersonalTask() {
        String assignee = "越哥";
        List<Task> list = taskService.createTaskQuery()//创建任务查询对象
                .taskAssignee(assignee)//指定个人任务查询，指定办理人
                .list();
        if (null != list && list.size() > 0) {
            for (Task task : list) {
                System.out.println("任务ID : "+ task.getId());
                System.out.println("任务名称 : "+ task.getName());
                System.out.println("任务的创建时间 : "+ task.getCreateTime());
                System.out.println("任务的办理人 : "+ task.getAssignee());
                System.out.println("流程实例ID : "+ task.getProcessInstanceId());
                System.out.println("执行对象ID : "+ task.getExecutionId());
                System.out.println("流程定义D : "+ task.getProcessDefinitionId());
                System.out.println("###############################");
            }
        }

    }

    //完成我的任务
    @Test
    public void completeMyPersonalTask() {
        //任务ID
        String taskId = "77504";
        //完成任务的同时,设置流程变量,使用流程变量用来指定完成任务后,下一个连线,对应sequenceFlow.bpmn文件中的${message=='不重要'}
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("message", "重要");
        taskService.complete(taskId, variables);
        System.out.println("完成任务: 任务ID :" + taskId);
    }

}
