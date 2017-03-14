[#ftl]
[@b.head/]
[@b.toolbar title="校外考试报名(第一步)"]
    bar.addBack();
[/@]
[#if (configs?size==0)]
          现在还没有开始开放报名。
[#else]
    <table width="100%" style="background-color:#FEFFCF"  align="center">
       <tr>
            <td width="15%">
                <img width="80px" height="110px" src="${avatarUrl}" alt="${(student.person.formatedName)!}" title="${(student.person.formatedName)!}"/>
            </td>
             <td width="85%">
                ${(student.person.formatedName)!}同学(${(student.code)!})，您好<br>
                <font color='red'>
                    你的身份证为:${(student.person.code)!'系统中暂时没有你的身份证号码'}<br>
                    如没有显示或数据有误请及时联系学院办公室，以免耽误你的报名。
                </font>
            </td>
        </tr>
    </table>
    [@b.messages/]
    [@b.grid items=signupList var="signup" ]
         <div align="center"><br><B>已有的报名记录为：</B><br></div>
        [@b.row]
            [@b.col title="学年学期" width="20%"]${signup.semester.schoolYear}学年 ${signup.semester.name}学期[/@]
            [@b.col property="subject.name" title="报名科目"/]
            [@b.col property="feeOfSignup" title="报名费"/]
            [@b.col property="feeOfOutline" title="考纲费"/]
            [@b.col property="feeOfMaterial" title="材料费"/]
            [@b.col property="signupAt" title="报名时间"]${(signup.signupAt?string("yyyy-MM-dd HH:mm:ss"))?if_exists}[/@]
            [@b.col property="campus.name" title="考试校区"/]
            [@b.col title="操作"][#if signup.payState.id == unpaid]<input type="button" onclick="cancelSignup(${(signup.id)!})" value="取消报名"/>[#if feeOpen]<input type="button" onclick="payForSignup(${(signup.id)!})" value="前往支付"/>[/#if][#else]已支付[/#if][/@]
        [/@]
    [/@]

    [#list configs as config]
     <div align="center">
      <br><B>&nbsp;${(config.name)!}&nbsp;(报名时间：${(config.beginAt?string("yyyy-MM-dd HH:mm"))!}~${(config.endAt?string("yyyy-MM-dd HH:mm"))!})</B>
       请选择以下报名科目：
      </div>
        <table class="gridtable" width="100%"  align="center">
        <thead class="gridhead">
            <tr class="darkColumn" align="center">
                <th width="20%">报名科目</th>
                <th width="5%">报名费</th>
                <th width="5%">材料费</th>
                <th width="5%">考纲费</th>
                <th width="20%">考试时间</th>
                <th width="5%">最大<br>学生数</th>
                <th width="8%">限制条件</th>
                <th width="18%">要求通过的科目</th>
                <th width="14%">点击即可报名</th>
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
                <td>${(setting.feeOfSignup)!}</td>
                <td>${(setting.feeOfMaterial)!}</td>
                <td>${(setting.feeOfOutline)!}</td>
                <td>[#if setting.examOn??]${setting.examOn?string("yyyy-MM-dd")} ${setting.examBeginAt!}~${(setting.examEndAt)!}[/#if]</td>
                <td>[#if setting.maxStd?default(0)!=0]${(setting.maxStd)!}[/#if]</td>
                <td>[#list setting.conditions as con] ${con.inclusive?string('允许','限制')}:${(con.grades)!('无')}  ${(con.education.name)!}[#if con_has_next]<br>[/#if][/#list]</td>
                <td>${(setting.superSubject.name)!}</td>
                <td align="center">
                [#if (!signupSubjects?? || !signupSubjects?seq_contains(setting.subject))]
                    <input type="button" onclick="signup(${(setting.id)!},'${setting.subject.name}')" value="报名"/>
                [#else]
                    已报名
                [/#if]
                </td>
            </tr>
        [/#list]
        </tbody>
        </table>
      [/#list]
[/#if]

[@b.form name="actionForm" method="post" action="!notice" target="main"/]
 
<script language="javascript">
    function cancelSignup(signupId){
        if(confirm("确定取消你选择的科目")){
            document.actionForm.action="index!cancelSignup.action?signupId="+signupId;
            bg.form.submit(document.actionForm);
        }
    }
   
       function payForSignup(signupId){
        bg.form.submit(document.actionForm,"${b.url('index!payment')}?signupId="+signupId,"_blank");
       }
   
    function signup(settingId,subject){
            if(confirm("确认你的身份证等各种信息数据都准确无误，继续报名["+subject+"]请按确定")){
                bg.form.addInput(document.actionForm,"setting.id",settingId);
                bg.form.submit(document.actionForm);
            }
    }
</script>
[@b.foot/]