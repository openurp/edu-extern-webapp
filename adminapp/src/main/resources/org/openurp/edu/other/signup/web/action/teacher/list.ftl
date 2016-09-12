<#include "/template/head.ftl"/>
<BODY LEFTMARGIN="0" TOPMARGIN="0">
<table id="bar"></table>
<@table.table id="id" sortable="true" width="100%">
 <@table.thead>
  <@table.selectAllTd id="otherExamSignUpId"/>
  <@table.sortTd id="otherExamSignUp.std.code" text="学号"/>
  <@table.sortTd id="otherExamSignUp.std.name" text="姓名"/>
  <@table.sortTd id="otherExamSignUp.category.name" text="考试科目"/>
  <@table.sortTd id="otherExamSignUp.feeOfSignUp" text="报名费"/>
  <@table.sortTd id="otherExamSignUp.feeOfMaterial" text="材料费"/>
  <@table.sortTd id="otherExamSignUp.feeOfOutline" text="考纲费"/>
  <@table.sortTd id="otherExamSignUp.district.name" text="校区"/>
  <@table.sortTd id="otherExamSignUp.semester.name" text="学期"/>
 </@>
 <@table.tbody datas=otherExamSignUps;otherExamSignUp>
  <@table.selectTd id="otherExamSignUpId" value=otherExamSignUp.id/>
  <td><a href="${base}/studentSearch!info.action?studentId=${otherExamSignUp.std.id}" title="学生详细信息">${otherExamSignUp.std.code}</a></td>
  <td><a href="${base}/signUpByTeacher!info.action?otherExamSignUp.id=${otherExamSignUp.id}" title="报名详情">${otherExamSignUp.std.name}</a></td>
  <td>${otherExamSignUp.category.name}</td>
  <td>${otherExamSignUp.feeOfSignUp?default("")}</td>
  <td>${otherExamSignUp.feeOfMaterial?default("")}</td>
  <td>${otherExamSignUp.feeOfOutline?default("")}</td>
  <td>${(otherExamSignUp.district.name)?default("")}</td>
  <td>${otherExamSignUp.semester.schoolYear}&nbsp;${otherExamSignUp.semester.name}</td>
 </@>
</@>
<@htm.actionForm name="actionForm" method="post" entity="otherExamSignUp" action="signUpByTeacher.action">
    <input type="hidden" name="configId" id="configId" />
</@>
<script language="javascript">
   var bar=new ToolBar('bar','校外考试报名维护',null,true,true);
   bar.setMessage('<@getMessage/>');
   bar.addItem("新增","add()","new.gif"); 
   bar.addItem("修改","edit()","update.gif");
   bar.addItem("删除","remove()","delete.gif");
   m=bar.addMenu("<@text name="action.export"/>","exportData()");
   m.addItem("按四六级格式导出","exportCET()");
   m.addItem("按计算机格式导出","exportComputer()");
   m.addItem("按普通话格式导出","exportPTH()");
   bar.addPrint("<@text name="action.print"/>");
   
   var form =document.actionForm;
   
   function exportCommon(){
      if(${totalSize}>10000){alert("数据量超过一万不能导出");return;}
      exportList();
   }
   function exportData(){
     addInput(form,"keys","std.code,std.name,std.grade,std.gender.name,std.person.code,std.major.name,std.major.code,std.department.name,category.name,category.code,semester.name,feeOfSignUp,feeOfMaterial,feeOfOutline,total,signUpAt,district.name");
     addInput(form,"titles","学号,姓名,年级,性别,身份证号,专业,专业代码,所属院系,科目,科目代码,学期,报名费,材料费,考纲费,合计,报名时间,考试校区");
     addInput(form,"fileName","校外考试报名数据");
     exportCommon();
   }
   
   function exportCET(){
     addInput(form,"keys","std.name,std.gender.name,std.code,std.person.code,std.educationType.code,std.duration,std.enrollOn,std.grade,std.department.name,std.major.name,std.majorClass.name");
     addInput(form,"titles","姓名,性别,学号,证件号码,学历,学制,入学年份,年级,院系,专业,班级");
     addInput(form,"fileName","CET报名数据");
     exportCommon();
   }
   
   function exportComputer(){
     addInput(form,"keys","std.name,std.gender.name,std.person.code,std.department.code,std.major.subject.code,std.major.code,std.enrollOn,std.duration,std.majorClass.name,std.code,category.code");
     addInput(form,"titles","姓名,性别,证件号码,学院代码,学科门类,专业,入学年份,学制,班级,学号,语言级别");
     addInput(form,"fileName","计算机报名数据");
     exportCommon();
   }
   
   function exportPTH(){
     addInput(form,"keys","std.name,std.person.code,std.gender.name,std.basicInfo.nation.name,std.basicInfo.birthday,std.code,std.department.name");
     addInput(form,"titles","姓名,身份证,性别,民族,出生年月,学号,院系");
     addInput(form,"fileName","普通话报名数据");
     exportCommon();
   }
 </script>
</body>
<#include "/template/foot.ftl"/>