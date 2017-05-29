<#include "/template/head.ftl"/>
<script type='text/javascript' src='${base}/dwr/interface/studentService.js'></script>
<script type='text/javascript' src='${base}/dwr/interface/examSignupService.js'></script>
<BODY LEFTMARGIN="0" TOPMARGIN="0">
<table id="bar" width="100%"></table>
<table class="formTable"  width="95%" align="center">
<form name="actionForm" method="post" action="signupByTeacher.action?method=save" >
    <input type="hidden" name="examSignupId" value="${examSignup.id?if_exists}"/>
    <@searchParams/>
 <tr>
  <td class="title" width="15%"><font color="red">*</font>学号</td>
  <td width="35%">
     <#if (examSignup.std.id)?exists>${examSignup.std.user.code}
     <input name="examSignup.std.id" value="${examSignup.std.id}" type="hidden"/>
     <#else>
      <input name="examSignup.std.user.code" maxLength="15" value=""/><button onclick="searchStd()">查找学生</button>
      <input name="examSignup.std.id" value="" type="hidden"/>
      </#if>
  </td>
  <td class="title">姓名</td>
  <td id="stdName">${(examSignup.std.name)?if_exists}</td>
 </tr>
 <tr>
  <td class="title"width="15%">报名费</td>
  <td width="35%"><input name="examSignup.feeOfSignup" value="${examSignup.feeOfSignup?if_exists}" maxLength="10"/></td>
  <td class="title"width="15%">考纲费</td>
  <td width="35%"><input name="examSignup.feeOfOutline" value="${examSignup.feeOfOutline?if_exists}" maxLength="10"/></td>
 </tr>
 <tr>
    <td class="title"width="15%">材料费</td>
    <td width="35%"><input name="examSignup.feeOfMaterial" value="${examSignup.feeOfMaterial?if_exists}" maxLength="10"/></td>
    <td class="title" width="15%">学期</td>
       <td>
    <select name="examSignup.semester.id" style="width:170px">
    <option value="">....</option>
    <#list semesters?sort_by("code")?reverse as se>
    <option value="${se.id}" <#if se.id?string=(examSignup.semester.id)?default('')?string>selected</#if> title="${se.schoolYear}&nbsp;${se.name}">${se.schoolYear}&nbsp;${se.name}</option>
    </#list>
    </select>
    </td>
</tr>
<tr>
  <td class="title"><font color="red">*</font>考试科目</td>
  <td  colspan="3">
     <select name="examSignup.category.id" style="width:150px">
         <#list examCategories as category>
         <option value="${category.id}" <#if (examSignup.category.id)?default("")?string=category.id?string>selected</#if> >${category.name}</option>
         </#list>
     </select>
  </td>
  </tr>
  <tr>
      <td class="title"><font color="red">*</font>考试校区:</td>
    <td colspan="3">
      <select name="examSignup.district.id" id="districtId" style="width:150px">
            <#list campuss as district>
                <option value="${district.id}" <#if (examSignup.district.id)?default("")?string=district.id?string>selected</#if> >${district.name}</option>
            </#list>
      </select>
    </td>
 </tr>
 <#if (examSignup.signupAt)?exists>
 <tr>
 <td class="title">报名时间</td>
 <td colspan="3">${examSignup.signupAt?if_exists}</td>
 </tr>
 </#if>
 </tr>
 <tr class="darkColumn">
  <td colspan="6" align="center"><button onclick="save(this.form)">提交</button>
  <button onclick="addInput(form,'addNext','1'),save(this.form)">添加下一个</button></td>
 </tr>
 </form>
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