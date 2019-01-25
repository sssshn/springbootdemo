package com.sss.activiti.e_historyQuery;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HistoryQueryTest {

    @Autowired
    private HistoryService historyService; //与历史数据(历史表相关的Service)

    //查询历史流程实例(后面讲)
    @Test
    public void findHistoryProcessInstance() {
        String processInstanceId = "52501";
        HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()//创建历史流程实例
                .processInstanceId(processInstanceId)//使用流程实例ID查询
                .orderByProcessInstanceStartTime().asc()
                .singleResult();
        System.out.println(hpi.getId()+" "+hpi.getProcessDefinitionId()+" "+hpi.getStartTime()+" "+hpi.getEndTime()+" "+hpi.getDurationInMillis());
    }

    //查询历史活动
    @Test
    public void findHistoryActiviti (){
        String processInstanceId = "52501";
        List<HistoricActivityInstance> list = historyService.createHistoricActivityInstanceQuery()//创建历史活动实例的查询
                .processInstanceId(processInstanceId)
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();
        if (list != null && list.size() > 0) {
            for (HistoricActivityInstance hai : list) {
                System.out.println(hai.getId()+"   "+hai.getProcessInstanceId()+"   "+hai.getActivityType()+"   "+hai.getStartTime()+"   "+hai.getEndTime()+"   "+hai.getDurationInMillis());
                System.out.println("*****************");
            }
        }
    }

    //查询历史任务
    @Test
    public void findHistoryTask() {
        String processInstanceId = "52501";
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()//创建历史任务实例
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceStartTime().asc()
                .list();
        if (list != null && list.size() > 0) {
            for (HistoricTaskInstance hti : list) {
                System.out.println(hti.getId()+"   "+hti.getName()+"   "+hti.getProcessInstanceId()+"   "+hti.getStartTime()+"   "+hti.getEndTime()+"   "+hti.getDurationInMillis());
                System.out.println("#################");
            }
        }
    }

    //查询历史流程变量
    @Test
    public void findHistoryProcessVariables (){
        String processInstanceId = "52501";
        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery()//创建一个历史的流程变量查询对象
                .processInstanceId(processInstanceId)
                .list();
        if (list != null && list.size() > 0) {
            for (HistoricVariableInstance hvi : list) {
                System.out.println(hvi.getId()+"     "+hvi.getProcessInstanceId()+"     "+hvi.getVariableTypeName()+"      "+hvi.getVariableName()+"       "+hvi.getValue());
                System.out.println("###################");
            }
        }
    }

}
