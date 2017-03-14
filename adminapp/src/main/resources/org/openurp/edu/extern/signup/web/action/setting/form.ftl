[#ftl]
[@b.head/]
[@b.toolbar title="考试科目维护"]
    bar.addBack("${b.text("action.back")}");
    function clearNoNum(obj)
    {
        obj.value = obj.value.replace(/[^\d.]/g,"");
        obj.value = obj.value.replace(/^\./g,"");
        obj.value = obj.value.replace(/\.{2,}/g,".");
        obj.value = obj.value.replace(".","$#$").replace(/\./g,"").replace("$#$",".");
    }
    
[/@]
[@b.form name="examSignupSettingForm" action="!save" title="考试科目维护" theme="list"]
    [@b.select name="examSignupSetting.subject.id" label="报考科目" required="true" style="width:150px" items=subjects?sort_by("name") empty="..."  value=(examSignupSetting.subject)?if_exists/]
    [@b.textfield name="examSignupSetting.feeOfSignup" label="报名费" value="${(examSignupSetting.feeOfSignup)!}" maxLength="10" required="true" style="width:150px" onBlur="clearNoNum(this)"/]
    [@b.textfield name="examSignupSetting.feeOfMaterial" label="材料费" value="${(examSignupSetting.feeOfMaterial)!}" maxLength="10" required="true" style="width:150px" onBlur="clearNoNum(this)"/]
    [@b.textfield name="examSignupSetting.feeOfOutline" label="考纲费" value="${(examSignupSetting.feeOfOutline)!}" maxLength="10" required="true" style="width:150px" onBlur="clearNoNum(this)"/]
    [@b.datepicker label="考试日期"  name="examSignupSetting.examOn" required="false" value=examSignupSetting.examOn! style="width:150px"/]
    [@b.datepicker label="开始时间" name="examBeginAt" id="sTime" maxDate="#F{$dp.$D(\\'eTime\\')}" value="${(examSignupSetting.examBeginAt)!}" format="HH:mm" required="false" /]
    [@b.datepicker label="结束时间" name="examEndAt" id="eTime" minDate="#F{$dp.$D(\\'sTime\\')}" value="${(examSignupSetting.examEndAt)!}" format="HH:mm" required="false" /]
    [@b.radios name="examSignupSetting.reExamAllowed"  label="是否能够重考" value=(examSignupSetting.reExamAllowed)!false?string('1','0') /]
    [@b.select name="examSignupSetting.superSubject.id" label="必须通过的科目" style="width:150px"]
        <option value="">....</option>
        [#list superSubjects?sort_by("name") as category]
            <option value="${category.id}" [#if (examSignupSetting.superSubject.id)?default("")?string=category.id?string]selected[/#if]>
               ${category.name}
              </option>
         [/#list]
    [/@]
    [@b.field label="白名单"]
        <textarea name="permitStds" style="width:200px;height:100px">${permitSeq!}</textarea>
    [/@]
    [@b.field label="黑名单"]
        <textarea name="forbiddenStds" style="width:200px;height:100px">${forbiddenSeq!}</textarea>
    [/@]
    [@b.textfield name="examSignupSetting.maxStd" label="最大报名人数" value="${(examSignupSetting.maxStd)!}" maxLength="4" required="true" style="width:150px" onBlur="clearNoNum(this)"/]
    
    [@b.formfoot]
        <input type="hidden" name="examSignupSetting.id" value="${examSignupSetting.id?if_exists}"/>
        <input type="hidden" name="examSignupSetting.config.id" value="${(examSignupSetting.config.id)!}"/>
        <input type="hidden" name="config.id" value="${(examSignupSetting.config.id)!}"/>
        
        [@b.submit value="action.submit"/]
        <input type="reset"  name="reset1" value="${b.text("action.reset")}" />
    [/@]
[/@]

<script language="JavaScript">
    jQuery(document).ready(function(){
        jQuery("select[name='examSignupSetting.subject.id']").change(function(){
            jQuery("select[name='examSignupSetting.superSubject.id']").empty();
            jQuery("select[name='examSignupSetting.superSubject.id']").append("<option value=''>...</option>")
            [#list superSubjects?sort_by("name") as category]
                jQuery("select[name='examSignupSetting.superSubject.id']").append("<option value='${category.id}' [#if (examSignupSetting.superSubject.id)?default('')?string=category.id?string]selected[/#if]>${category.name}</option>");
             [/#list]
             jQuery("select[name='examSignupSetting.superSubject.id'] option[value='"+jQuery(this).val()+"']").remove();
        })
    })
</script>
[@b.foot/]