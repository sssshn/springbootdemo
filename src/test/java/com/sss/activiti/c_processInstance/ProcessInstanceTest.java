package com.sss.activiti.c_processInstance;

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
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 *
 * 一个流程中,执行对象可以有多个,但是流程定义只能有一个
 * 当流程定义规则只执行一次的时候,那么流程实例就是执行对象
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProcessInstanceTest {

    @Autowired
    private RepositoryService repositoryService;    //与流程定义和部署对象相关的service

    @Autowired
    private RuntimeService runtimeService;  //与正在执行的流程实例和执行对象相关的service

    @Autowired
    private TaskService taskService;    //与正在执行的任务管理相关的service

    @Autowired
    private HistoryService historyService;  //与历史数据(从历史表)相关的service

    //手动部署流程定义(从zip)
    @Test
    public void deploymentProcessDefinition_Zip() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("process/helloworld.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        Deployment deploy = repositoryService.createDeployment()//创建一个部署对象
                .name("流程定义")//添加部署的名称
                .addZipInputStream(zipInputStream)//指定zip格式的文件完成部署
                .deploy();//完成部署
        System.out.println("流程部署ID： " + deploy.getId());
        System.out.println("流程部署名称： " + deploy.getName());
    }

    //启动流程实例
    @Test
    public void startProcessInstance() {
        //流程定义的key
        String key = "helloworld";
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(key);//使用流程定义的key启动流程实例，key对应的helloworld文件中的id属性值,使用key启动，默认是按照最新版本的流程定义启动
        System.out.println("流程实例ID： " + processInstance.getId());
        System.out.println("流程定义ID： " + processInstance.getProcessDefinitionId());
    }

    //查询当前人的个人任务
    @Test
    public void findMyPersonalTask() {
        String assignee = "腾讯爸爸";
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
        String taskId = "30002";
        taskService.complete(taskId);
        System.out.println("完成任务: 任务ID :" + taskId);
    }

    //判断流程状态(判断流程正在执行,还是结束)
    @Test
    public void isProcessEnd() {
        String processInstanceId = "25001";
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()//创建流程实例查询
                .processInstanceId(processInstanceId)//使用流程ID查询
                .singleResult();
        if (processInstance == null) {
            System.out.println("流程已经结束");
        }else {
            System.out.println("流程没有结束");
        }
    }

    //查询历史任务(后面讲)
    @Test
    public void findHistoryTask() {
        String taskAssignee = "杨超越";
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()//创建历史任务实例查询
                .taskAssignee(taskAssignee)//指定历史任务的办理人
                .list();
        if (list != null && list.size() > 0) {
            for (HistoricTaskInstance hti : list
                 ) {
                System.out.println(hti.getId()+"  "+hti.getName()+"  "+hti.getProcessDefinitionId()+" "+hti.getStartTime()+"  "+hti.getEndTime()+"  "+hti.getDurationInMillis());
                System.out.println("****************");
            }
        }
    }

    //查询历史流程实例(后面讲)
    @Test
    public void findHistoryProcessInstance() {
        String processInstanceId = "25001";
        HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()//创建历史流程实例
                .processInstanceId(processInstanceId)//使用流程实例ID查询
                .singleResult();
        System.out.println(hpi.getId()+" "+hpi.getProcessDefinitionId()+" "+hpi.getStartTime()+" "+hpi.getEndTime()+" "+hpi.getDurationInMillis());
    }

}
