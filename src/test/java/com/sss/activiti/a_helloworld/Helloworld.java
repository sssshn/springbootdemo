package com.sss.activiti.a_helloworld;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
/**
 * activiti工作流helloworld测试
 */
public class Helloworld {

    @Autowired
    private RepositoryService repositoryService;    //与流程定义和部署对象相关的service

    @Autowired
    private RuntimeService runtimeService;  //与正在执行的流程实例和执行对象相关的service

    @Autowired
    private TaskService taskService;    //与正在执行的任务管理相关的service

    @Test
    public void contextLoads() {

    }


    //手动部署流程
    @Test
    public void deploymentProcessDefinition() {
        Deployment deploy = repositoryService.createDeployment()//创建一个部署对象
                .name("helloworld入门程序")
                .addClasspathResource("process/helloworld.bpmn")//从classpath的资源中加载，一次只能加载一个文件
                .addClasspathResource("process/helloworld.png")//从classpath的资源中加载，一次只能加载一个文件
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
        String taskId = "7502";
        taskService.complete(taskId);
        System.out.println("完成任务: 任务ID :" + taskId);
    }

}
