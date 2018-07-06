<#include "/template/head.ftl"/>
<BODY LEFTMARGIN="0" TOPMARGIN="0">
<table id="bar" width="100%"></table>

<table class="infoTable"  width="95%" align="center">
 <tr>
  <td  rowspan="4"align="center"><img src="avatar/user!show.action?user.name=${examSignup.std.user.code}"  width="80px" height="110px"/>
  <td  rowspan="4">${examSignup.std.user.code}<br>${examSignup.std.name}</td>
  <td class="title" width="15%" >学期:</td>
  <td>${examSignup.semester.schoolYear} ${examSignup.semester.name}</td>
 </tr>
 <tr>
  <td class="title"width="15%">报名费:</td>
  <td width="35%">${examSignup.feeOfSignup!}</td>
 </tr>
 <tr>
  <td class="title"width="15%">考纲费:</td>
  <td width="35%">${examSignup.feeOfOutline!}</td>
 </tr>
 <tr>
  <td class="title"width="15%">材料费:</td>
  <td width="35%">${examSignup.feeOfMaterial!}</td>
 </tr>
 <tr>
  <td class="title">院系:</td>
  <td >${(examSignup.std.department.name)!}</td>
  <td class="title">考试科目:</td>
  <td >${examSignup.category.name}[${examSignup.category.code}]</td>
 </tr>
 <tr>
  <td class="title">专业:</td>
  <td id="stdName">${(examSignup.std.major.name)!}</td>
  <td class="title">考试校区:</td>
  <td>${examSignup.district.name}</td>
 </tr>
 <tr>
  <td class="title">报名时间:</td>
  <td>${(examSignup.signupAt?string("yyyy-MM-dd HH:mm:ss"))!}</td>
  <td class="title">准考证号:</td>
  <td>${(examSignup.examNo)!}</td>
 </tr>
</table>

<script language="javascript">
   var bar=new ToolBar('bar','校外考试报名信息',null,true,true);
   bar.setMessage('<@getMessage/>');
   bar.addBack("<@text name="action.back"/>");
   var form =document.actionForm;
   var isNeedDist;

   function searchStd(){
       studentService.getStudent(form['examSignup.std.user.code'].value,setData);
   }
   function setData(std){
     if(null!=std){
        form['examSignup.std.id'].value=std.id;
        $("stdName").innerHTML=std.name;
     }else{
        form['examSignup.std.id'].value="";
        $("stdName").innerHTML="查无此人";
     }
   }
   function save(form){
        if(isNeedDist==1){
            if(form['examSignup.district.id'].value==""){
             alert("校区不能为空");return;
            }
        }

        if(form['examSignup.category.id'].value==""){
             alert("科目不能为空");return;
        }
     if(form['examSignupId'].value==""&&form['examSignup.std.id'].value==""){
        alert("请输入学号");return;
     }else{
        //alert(form['examSignup.std.user.code'].value);
        form.submit();
     }
   }
</script>
</body>
<#include "/template/foot.ftl"/>
