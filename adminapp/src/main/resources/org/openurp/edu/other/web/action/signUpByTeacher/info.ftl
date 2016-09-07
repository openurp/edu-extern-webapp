<#include "/template/head.ftl"/>
<BODY LEFTMARGIN="0" TOPMARGIN="0">
<table id="bar" width="100%"></table>

<table class="infoTable"  width="95%" align="center">
 <tr>
  <td  rowspan="4"align="center"><img src="avatar/user!show.action?user.name=${otherExamSignUp.std.code}"  width="80px" height="110px"/>
  <td  rowspan="4">${otherExamSignUp.std.code}<br>${otherExamSignUp.std.name}</td>
  <td class="title" width="15%" >学期:</td>
  <td>${otherExamSignUp.semester.schoolYear} ${otherExamSignUp.semester.name}</td>
 </tr>
 <tr>
  <td class="title"width="15%">报名费:</td>
  <td width="35%">${otherExamSignUp.feeOfSignUp!}</td>
 </tr>
 <tr>
  <td class="title"width="15%">考纲费:</td>
  <td width="35%">${otherExamSignUp.feeOfOutline!}</td>
 </tr>
 <tr>
  <td class="title"width="15%">材料费:</td>
  <td width="35%">${otherExamSignUp.feeOfMaterial!}</td>
 </tr>
 <tr>
  <td class="title">院系:</td>
  <td >${(otherExamSignUp.std.department.name)!}</td>
  <td class="title">考试科目:</td>
  <td >${otherExamSignUp.category.name}[${otherExamSignUp.category.code}]</td>
 </tr>
 <tr>
  <td class="title">专业:</td>
  <td id="stdName">${(otherExamSignUp.std.major.name)!}</td>
  <td class="title">考试校区:</td>
  <td>${otherExamSignUp.district.name}</td>
 </tr>
 <tr>
  <td class="title">报名时间:</td>
  <td>${(otherExamSignUp.signUpAt?string("yyyy-MM-dd HH:mm:ss"))!}</td>
  <td class="title">准考证号:</td>
  <td>${(otherExamSignUp.examNo)!}</td>
 </tr>
</table>

<script language="javascript">
   var bar=new ToolBar('bar','资格考试报名信息',null,true,true);
   bar.setMessage('<@getMessage/>');
   bar.addBack("<@text name="action.back"/>");
   var form =document.actionForm;
   var isNeedDist;
   
   function searchStd(){
       studentService.getStudent(form['otherExamSignUp.std.code'].value,setData);
   }
   function setData(std){
     if(null!=std){
        form['otherExamSignUp.std.id'].value=std.id;
        $("stdName").innerHTML=std.name;
     }else{
        form['otherExamSignUp.std.id'].value="";
        $("stdName").innerHTML="查无此人";
     }
   }
   function save(form){
        if(isNeedDist==1){
            if(form['otherExamSignUp.district.id'].value==""){
             alert("校区不能为空");return;
            }
        }
        
        if(form['otherExamSignUp.category.id'].value==""){
             alert("科目不能为空");return;
        }
     if(form['otherExamSignUpId'].value==""&&form['otherExamSignUp.std.id'].value==""){
        alert("请输入学号");return;
     }else{
        //alert(form['otherExamSignUp.std.code'].value);
        form.submit();
     }
   }
</script>
</body>
<#include "/template/foot.ftl"/>