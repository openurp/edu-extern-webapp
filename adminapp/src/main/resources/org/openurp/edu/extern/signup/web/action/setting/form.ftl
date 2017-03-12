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
[@b.form name="otherExamSignUpSettingForm" action="!save" title="考试科目维护" theme="list"]
    [@b.select name="otherExamSignUpSetting.subject.id" label="报考科目" required="true" style="width:150px" items=subjects?sort_by("name") empty="..."  value=(otherExamSignUpSetting.subject)?if_exists/]
    [@b.textfield name="otherExamSignUpSetting.feeOfSignUp" label="报名费" value="${(otherExamSignUpSetting.feeOfSignUp)!}" maxLength="10" required="true" style="width:150px" onBlur="clearNoNum(this)"/]
    [@b.startend label="考试开放时间段" readOnly="readOnly"  name="otherExamSignUpSetting.beginAt,otherExamSignUpSetting.endAt" required="true" start=otherExamSignUpSetting.beginAt end=otherExamSignUpSetting.endAt format="yyyy-MM-dd HH:mm:ss" style="width:150px"/]
    [@b.textfield name="otherExamSignUpSetting.feeOfMaterial" label="材料费" value="${(otherExamSignUpSetting.feeOfMaterial)!}" maxLength="10" required="true" style="width:150px" onBlur="clearNoNum(this)"/]
    [@b.textfield name="otherExamSignUpSetting.feeOfOutline" label="考纲费" value="${(otherExamSignUpSetting.feeOfOutline)!}" maxLength="10" required="true" style="width:150px" onBlur="clearNoNum(this)"/]
    [@b.radios name="otherExamSignUpSetting.reExamAllowed"  label="是否能够重考" value=(otherExamSignUpSetting.reExamAllowed)!false?string('1','0') /]
    [@b.select name="otherExamSignUpSetting.superSubject.id" label="必须通过的科目" style="width:150px"]
        <option value="">....</option>
        [#list superSubjects?sort_by("name") as category]
            <option value="${category.id}" [#if (otherExamSignUpSetting.superSubject.id)?default("")?string=category.id?string]selected[/#if]>
               ${category.name}
              </option>
         [/#list]
    [/@]
    [#--[@b.radios name="otherExamSignUpSetting.fieldVisable" label="显示扩展信息" value=(otherExamSignUpSetting.fieldVisable)!false?string('1','0') /]--]
    [@b.field label="白名单"]
        <textarea name="permitStds" style="width:200px;height:100px">${permitSeq!}</textarea>
    [/@]
    [@b.field label="黑名单"]
        <textarea name="forbiddenStds" style="width:200px;height:100px">${forbiddenSeq!}</textarea>
    [/@]
    [@b.field label="<select name='otherExamSignUpSetting.gradePermited' id='gradePermited'><option value='0'>限制</option><option value='1'>允许</option></select>年级"]
        <input name="otherExamSignUpSetting.grade" value="${(otherExamSignUpSetting.grade)!}" maxLength="30" style="width:150px" />        
    [/@]
    [@b.textfield name="otherExamSignUpSetting.maxStd" label="最大报名人数" value="${(otherExamSignUpSetting.maxStd)!}" maxLength="4" required="true" style="width:150px" onBlur="clearNoNum(this)"/]
    
    [@b.formfoot]
        <input type="hidden" name="otherExamSignUpSetting.id" value="${otherExamSignUpSetting.id?if_exists}"/>
        <input type="hidden" name="otherExamSignUpSetting.config.id" value="${(otherExamSignUpSetting.config.id)!}"/>
        <input type="hidden" name="config.id" value="${(otherExamSignUpSetting.config.id)!}"/>
        
        [@b.submit value="action.submit"/]
        <input type="reset"  name="reset1" value="${b.text("action.reset")}" />
    [/@]
[/@]
[#if otherExamSignUpSetting.gradePermited]
<script language="JavaScript">
    jQuery(document).ready(function(){
        jQuery("#gradePermited").val('1');
    })
</script>
[/#if]

<script language="JavaScript">
    jQuery(document).ready(function(){
        jQuery("select[name='otherExamSignUpSetting.subject.id']").change(function(){
            jQuery("select[name='otherExamSignUpSetting.superSubject.id']").empty();
            jQuery("select[name='otherExamSignUpSetting.superSubject.id']").append("<option value=''>...</option>")
            [#list superSubjects?sort_by("name") as category]
                jQuery("select[name='otherExamSignUpSetting.superSubject.id']").append("<option value='${category.id}' [#if (otherExamSignUpSetting.superSubject.id)?default('')?string=category.id?string]selected[/#if]>${category.name}</option>");
             [/#list]
             jQuery("select[name='otherExamSignUpSetting.superSubject.id'] option[value='"+jQuery(this).val()+"']").remove();
        })
    })
</script>
[@b.foot/]