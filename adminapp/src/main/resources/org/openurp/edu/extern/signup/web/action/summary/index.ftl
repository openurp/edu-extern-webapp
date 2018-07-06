<#include "/template/head.ftl"/>
<link href="css/tab.css" rel="stylesheet" type="text/css">
<script language="JavaScript" type="text/JavaScript" src="${base}/static/scripts/itemSelect.js"></script>
<BODY LEFTMARGIN="0" TOPMARGIN="0">
    <table id="examSignupSummaryBar"></table>
    <table class="frameTable_title" width="100%">
      <tr>
           <form name="searchForm" method="post" action="examSignupSummary.action?method=index" >
           <td>考试类别:
              <select name="examKind.id" onChange="examKind()">
                  <option value=false>全部</option>
                 <#list examKindList?sort_by("id")?reverse  as examKind>
                 <option value="${(examKind.id)?if_exists}">${(examKind.name)?if_exists}</option>
                 </#list>
              </select>
           </td>
           <td></td>
               <#include "/template/time/semester.ftl"/>
           </form>
          </tr>
    </table>
      <table border="0"  class="frameTable" width="100%" height="90%" >
      <tr>
        <td valign="top" class="frameTable_view" width="15%" style="font-size:10pt">
          <table  width="100%" id ="viewTables" style="font-size:10pt">
           <tr>
             <td class="padding" id="defaultSelectItem" onclick="search(this,'signupSummaryByDept')"  onmouseover="MouseOver(event)" onmouseout="MouseOut(event)">
             &nbsp;&nbsp;<image src="${base}/static/images/action/list.gif">报考人数院系统计
             </td>
           </tr>


           <tr>
           <tr>
                   <tr>
                <td >
                   <form name="sumForm" method="post" action="examSignupSummary.action?method=setDateSpan&semester.id=${semester.id}" target="examInfoSetting">
                 </form>
                 </td>
                 </tr>


          </table>
      <td>
      <td valign="top" colspan="12">
          <table border="0" class="frameTable" width="100%" height="100%" >
              <tr>
                  <td valign="top">
                       <iframe  src="#"
                     id="examSignupFrame" name="examSignupFrame"
                     marginwidth="0" marginheight="0" scrolling="no"
                     frameborder="0"  height="100%" width="100%">
                     </iframe>
                  </td>
              </tr>
          </table>
      </td>
      </tr>
     </table>
    <script>
           var bar = new ToolBar("examSignupSummaryBar","报名查询",null,true,true);
           form=document.sumForm;
           action="examSignupSummary.action";
           document.getElementById("defaultSelectItem").onclick();
           function search(td,what){
              clearSelected(viewTables,td);
              setSelectedRow(viewTables,td);
              form.action=action+"?method="+what;
              form.target="examSignupFrame";
              transferParams(document.searchForm,form,null,false);
              form.submit();
           }

           function examKind(){
                 document.getElementById("defaultSelectItem").onclick();
           }

    </script>
</body>
<#include "/template/foot.ftl"/>

