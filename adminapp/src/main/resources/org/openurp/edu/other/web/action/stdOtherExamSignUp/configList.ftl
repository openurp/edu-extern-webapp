[#ftl]
[@b.head/]
[@b.toolbar title="资格考试报名(第一步)"]
    bar.addBack();
[/@]
[#if (configs?size==0)]
          现在还没有开始开放报名。
[#else]
    <table width="100%" style="background-color:#FEFFCF"  align="center">
       <tr>
            <td width="15%">
                <img width="80px" height="110px" src="${base}/avatar/my.action" alt="${(student.person.name)!}" title="${(student.person.name)!}"/>
            </td>
             <td width="85%">
                ${(student.person.name)!}同学(${(student.code)!})，您好<br>
                <font color='red'>
                    你的身份证为:${(student.person.idcard)!'系统中暂时没有你的身份证号码'}<br>
                    如没有显示或数据有误请及时联系学院办公室，以免耽误你的报名。
                </font>
            </td>
        </tr>
    </table>
    [@b.messages/]
    [@b.grid items=signUpList var="signUp" ]
         <div align="center"><br><B>已有的报名记录为：</B><br></div>
        [@b.row]
            [@b.col title="学年学期" width="20%"]学年(${(signUp.semester.schoolYear)!})--学期(${(signUp.semester.name)!})[/@]
            [@b.col property="subject.name" title="报名科目"/]
            [@b.col property="feeOfSignUp" title="报名费"/]
            [@b.col property="feeOfOutline" title="考纲费"/]
            [@b.col property="feeOfMaterial" title="材料费"/]
            [@b.col property="signUpAt" title="报名时间"]${(signUp.signUpAt?string("yyyy-MM-dd HH:mm:ss"))?if_exists}[/@]
            [@b.col property="campus.name" title="考试校区"/]
            [@b.col title="操作"][#if signUp.payState.id == unpaid]<input type="button" onclick="cancelSignUp(${(signUp.id)!})" value="取消报名"/>[#if feeOpen]<input type="button" onclick="payForSignUp(${(signUp.id)!})" value="前往支付"/>[/#if][#else]已支付[/#if][/@]
        [/@]
    [/@]
    [@b.grid items=signUpList var="signUp" ]
         <div align="center"><br><B>请选择以下报名科目：</B><br></div>
        <table class="gridtable" width="100%"  align="center">
        [#list configs as config]
        <thead class="gridhead">
            <tr class="darkColumn"><th colspan="11" align="left">&nbsp;${(config.name)!}&nbsp;(报名时间：${(config.beginAt?string("yyyy-MM-dd HH:mm"))!}~${(config.endAt?string("yyyy-MM-dd HH:mm"))!})</th></tr>
            <tr class="darkColumn" align="center">
                <th>报名科目</th>
                <th>报名费</th>
                <th>材料费</th>
                <th>考纲费</th>
                <th colspan="3">考试时间</th>
                <th>最大学生数</th>
                <th>限制年级</th>
                <th>要求通过的科目</th>
                <th>点击即可报名</th>
            </tr>
        </thead>
        <tbody>
        [#list config.settings?sort_by(["subject","code"]) as setting]
            [#if setting_index % 2 == 0]
                [#assign lessonClass="griddata-even"/]
            [#else]
                [#assign lessonClass="griddata-odd"/]
            [/#if]
            <tr class="${lessonClass!}">
                <td>${(setting.subject.name)!}</td>
                <td>${(setting.feeOfSignUp)!}</td>
                <td>${(setting.feeOfMaterial)!}</td>
                <td>${(setting.feeOfOutline)!}</td>
                <td colspan="3">${(setting.beginAt?string("yyyy-MM-dd HH:mm "))!}-${(setting.endAt?string("yyyy-MM-dd HH:mm"))!}</td>
                <td>[#if setting.maxStd?default(0)!=0]${(setting.maxStd)!}[/#if]</td>
                <td>${(setting.grade)!}</td>
                <td>${(setting.superSubject.name)!}</td>
            <td align="center">
                [#if (!signUpSubjects?? || !signUpSubjects?seq_contains(setting.subject))]
                    <input type="button" onclick="signUp(${(setting.id)!})" value="报名"/>
                [#else]
                    已报名
                [/#if]
            </td>
            </tr>
        [/#list]
        </tbody>
        [/#list]
        </table>
    [/@]
[/#if]

[@b.form name="actionForm" method="post" action="!notice" target="main"/]
 
<script language="javascript">
    function cancelSignUp(signUpId){
        if(confirm("确定取消你选择的科目")){
            document.actionForm.action="stdOtherExamSignUp!cancelSignUp.action?signUpId="+signUpId;
            bg.form.submit(document.actionForm);
        }
    }
   
       function payForSignUp(signUpId){
        bg.form.submit(document.actionForm,"${b.url('stdOtherExamSignUp!payment')}?signUpId="+signUpId,"_blank");
       }
   
    function signUp(settingId){
        [#if (student.person.idcard)??]
            if(confirm("确认你的身份证等各种信息数据都准确无误，继续报名请按确定")){
                bg.form.addInput(document.actionForm,"setting.id",settingId);
                bg.form.submit(document.actionForm);
            }
        [#else]
            alert("你的身份证信息缺失,请到教务主管部门进行填写。");
        [/#if]
    }
</script>
[@b.foot/]