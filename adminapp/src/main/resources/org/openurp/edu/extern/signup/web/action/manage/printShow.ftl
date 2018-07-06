[#ftl/]
[@b.head /]
[@b.toolbar title="校外考试报名名单"][/@]
[#list examSignups?keys as Key]
<p>&nbsp;</p>
<h5>班级：${Key.code}&nbsp;&nbsp;&nbsp;&nbsp;人数:${examSignups.get(Key)?size}</h5>
<table  id="id" sortable="true" width="100%" class="gridtable">
     <thead class="gridhead">
      <td  width="2%">序号</td>
      <td  width="8%">学号</td>
      <td  width="8%">姓名</td>
      <td  width="8%">身份证号</td>
      <td  width="8%">考试科目</td>
      <td  width="8%">学期</td>
    </thead>
 [#assign index=0]
 [#list examSignups as examSignup]
     <tbody>
     <!--<tbody datas=examSignups.get(Key);examSignup>-->
     [#assign index=index+1]
          <td>${index}</td>
          <td>${examSignup.std.user.code}</td>
          <td>${examSignup.std.name}</td>
          <td>${(examSignup.std.person.code)?if_exists}</td>
          <td>${examSignup.category.name}</td>
          <td>${examSignup.semester.schoolYear}&nbsp;${examSignup.semester.name}</td>
     </tbody>
 [/#list]
</table>
[/#list]
[@b.form  name="actionForm" method="post" entity="examSignup" action="!printShow"]
     <input type="hidden" name="configId" id="configId" />
[/@]
<script language="javascript">
   function printpr() //预览函数
    {
        document.all("dayinDiv").style.display="none"; //打印之前先隐藏不想打印输出的元素（此例中隐藏“打印”和“打印预览”两个按钮）
        var OLECMDID = 7;
        var PROMPT = 1;
        var WebBrowser = '<OBJECT ID="WebBrowser1" WIDTH=0 HEIGHT=0 CLASSID="CLSID:8856F961-340A-11D0-A96B-00C04FD705A2"></OBJECT>';
        document.body.insertAdjacentHTML('beforeEnd', WebBrowser);
        WebBrowser1.ExecWB(OLECMDID, PROMPT);
        WebBrowser1.outerHTML = "";
        document.all("dayinDiv").style.display="";//打印之后将该元素显示出来（显示出“打印”和“打印预览”两个按钮，方便别人下次打印）
    }
 </script>
[@b.foot/]
