<#include "/template/head.ftl"/>
<script type='text/javascript' src='${base}/dwr/interface/otherExamSignUpService.js'></script>
<BODY LEFTMARGIN="0" TOPMARGIN="0">
<table id="bar"></table>
   <table class="frameTable">
   <tr>
    <td  style="width:160px"  class="frameTable_view">
    <form name="actionForm" action="signUpByTeacher!search.action" method="post" target="contentFrame">
     <table class="searchTable" onkeypress="dwr.util.onReturn(event, search)">
      <tr>
       <td>学号:</td><td><input name="otherExamSignUp.std.code" value="" style="width:100px" maxLength="15"></td>
      </tr>
      <tr>
       <td>姓名:</td><td><input name="otherExamSignUp.std.name" value="" style="width:100px" maxLength="15"></td>
      </tr>
      <tr>
       <td>年级:</td><td><input name="otherExamSignUp.std.grade" value="" style="width:100px" maxLength="6"></td>
      </tr>
      <tr>
       <td>院系:</td>
       <td>
         <select name="otherExamSignUp.std.department.id" style="width:100px" id="categoryId">
         <option value="">....</option>
         <#list departments?sort_by("code") as depar>
         <option value="${depar.id}" title="${depar.name}">${depar.name}</option>
         </#list>
         </select>
       </td>
     </tr>
     <tr>
       <td>考试类型:</td>
       <td>
         <select name="otherExamSignUp.category.kind.id" id="kindId" style="width:100px" onchange="kindIdSelect()">
         <option value="">....</option>
         <#list otherExamKinds as kind>
         <option value="${kind.id}" title="${kind.name}">${kind.name}</option>
         </#list>
         </select>
       </td>
      </tr>
      <tr>
       <td>考试科目:</td>
       <td>
         <select name="otherExamSignUp.category.id" style="width:100px" id="categoryId">
         <option value="">....</option>
         <#list otherExamCategories as category>
         <option value="${category.id}" title="${category.name}">${category.name}</option>
         </#list>
         </select>
       </td>
      </tr>
      <tr>
        <td style="width:60px;"><@text name="attr.year2year"/>:</td>
        <td style="width:100px;">
            <div style="display:none">
            <select id="academicCalendar" name="semester.calendar.id" style="width:100px;" >               
            <option value=""></option>
            </select>
            </div>
            <select id="year" name="otherExamSignUp.semester.schoolYear" style="width:100px;">
                <option value=""></option>
            </select>
        </td>
      </tr>
      <tr>
        <td style="width:50px;"><@text name="attr.term"/>:</td>
        <td style="width:50px;">
            <select id="term" name="otherExamSignUp.semester.name" style="width:100px;">
                <option value=""></option>
            </select>
       </td>
      </tr>
      <tr>
         <td>考试校区:</td>
        <td><select name="otherExamSignUp.district.id" style="width:100px">
            <option value="">....</option>
            <#list campuss as district>
                <option value="${district.id}" title="${district.name}">${district.name}</option>
            </#list>
        </td>
       </tr>
      <tr>
       <td>班级名称:</td>
       <td>
         <input name="adminClassesName" value="" style="width:100px"/>
       </td>
      </tr>
      <tr>
      <tr>
       <td colspan="2" align="center">
         <button onclick="this.form.submit()">查询</button>
       </td>
      </tr>
      </table>
      </form>
      </td>
    <td valign="top">
     <iframe  src="#"
     id="contentFrame" name="contentFrame" scrolling="no"
     marginwidth="0" marginheight="0"      frameborder="0"  height="100%" width="100%">
     </iframe>
    </td>
   </tr>
  </table>
<script language="javascript">
   var bar=new ToolBar('bar','资格考试报名维护',null,true,true);
   bar.setMessage('<@getMessage/>');
   function search(){
           document.actionForm.submit();
   }
   search();
 </script>
<#assign yearNullable=true>
<#assign termNullable=true>
<#include "/template/semesterSelect.ftl"/>
</body>
<#include "/template/foot.ftl"/>