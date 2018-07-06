[#ftl]
[@b.head/]
[#if config.notice?? && config.notice != ""]
[@b.toolbar title="校外考试报名(第二步)"]
     bar.addBack();
[/@]
<table>
  <thead>
  <br/>
       <p align="center"><font size="5"><b>考生诚信考试承诺书</b></font></p>
  </thead>
  <tbody>
    <tr>
        <td>
            <table border="0"><tr><td width="100px" height="300px"></td></tr></table>
        </td>
        <td style="word-break:break-all; word-wrap:break-word;">
            <font size="2">
             ${(config.notice)?default("")}
            </font>
        </td>
    </tr>
 </tbody>
</table>
[@b.form name="actionForm" method="post" action="!signupForm" target="main"]
        <table class="table" width="90%" align="center">
            <input type="hidden" name="config.id" value="${config.id}"/>
            <input type="hidden" name="setting.id" value="${Parameters['setting.id']}"/>
        <tr>
              <td align="center">
                <input type="button" id="bn1" value="同意"  onclick="bg.form.submit(this.form);"/>
                <input type="button" onclick="history.back();" value="不同意"/>
              </td>
        </tr>
        </table>
[/@]
[#else]
[@b.form name="actionForm" method="post" action="!signupForm" target="main"]
    <input type="hidden" name="config.id" value="${config.id}"/>
    <input type="hidden" name="setting.id" value="${Parameters['setting.id']}"/>
[/@]
<script>
    $(function(){
        bg.form.submit(document.actionForm,null,null,null,null,true);
    })
</script>
[/#if]
[@b.foot/]
