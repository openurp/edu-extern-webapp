[#ftl/]
[@b.head/]
    [@b.toolbar title="考试报名信息"]
        bar.addBack("${b.text("action.back")}");
    [/@]
[@b.form name="examSignupForm" action="!save"  theme="list"]
    [#if (examSignup.id)?exists]
        [@b.field label="std.code"]
            ${(examSignup.std.code)!}
            <input type="hidden" name="examSignup.std.code" value="${(examSignup.std.code)!}" />
        [/@]
    [#else]
        [@b.field name="findstudent"]
            [@b.textfield theme="xml" name="examSignup.std.code" maxlength="15"  id="stdCode" label="std.code" value="${(examSignup.std.code)!}" required="true" style="width:150px" comment="<input type='button' value='查询' onClick='searchStudent()'/>"/]
             <input type="hidden" id="stdId" name="examSignup.std.id" value="${(examSignup.std.id)!}" />
        [/@]
    [/#if]
    [@b.field label="姓名"]
      <table>
       <tr>
            <td id="stdName">${(examSignup.std.name)?default("&nbsp;")}</td>
       </tr>
      </table>
    [/@]
    [#--
    [@b.textfield name="examSignup.examNo" label="准考证号码" value="${(examSignup.examNo)!}" style="width:150px" onBlur="clearNoNum(this)"/]
    --]
    [@b.textfield name="examSignup.feeOfSignup" maxlength="10" label="报名费" value="${(examSignup.feeOfSignup)!}" style="width:150px" onBlur="clearNoNum(this)"/]
    [@b.textfield name="examSignup.feeOfOutline" maxlength="10" label="考纲费" value="${(examSignup.feeOfOutline)!}" style="width:150px" onBlur="clearNoNum(this)"/]
    [@b.textfield name="examSignup.feeOfMaterial" maxlength="10" label="材料费" value="${(examSignup.feeOfMaterial)!}" style="width:150px" onBlur="clearNoNum(this)"/]
    [@eams.semesterCalendar label="学年" name="examSignup.semester.id" value=(examSignup.semester)! empty=false/]
    [#--
    [@eams.projectUI name="project.id" label="项目" empty="false" semesterName="examSignup.semester.id" semesterEmpty="false" semesterValue=examSignup.semester /]
    --]
    [@b.select name="examSignup.subject.id" items=examSubjects empty="..." value=(examSignup.subject.id)! label="报名科目" style="width:150px" required="true"/]
    [@b.select name="examSignup.campus.id" items=campuses empty="..." value=(examSignup.campus.id)! label="考试校区" style="width:150px" required="true" /]
    [@b.field label="是否乘坐班车"]
        <select name="examSignup.takeBus" style="width:150px">
            <option value="1" [#if (examSignup.takeBus)]selected[/#if]>${b.text("common.yes")}</option>
            <option value="0" [#if (!examSignup.takeBus)]selected[/#if]>${b.text("common.no")}</option>
        </select>
    [/@]
    [#--
    [@b.datepicker label="entity.signupAt" name="examSignup.signupAt" value="${(examSignup.signupAt?string('yyyy-MM-dd'))!}" format="yyyy-MM-dd" requried="true" readOnly="readOnly"/]
    --]
    [@b.formfoot]
        <input type="hidden" name="examSignupId" value="${examSignup.id!}" />
        
        [@b.submit value="action.submit"/]
        <input type="button"  onclick="bg.form.addInput(form,'addNext','1'),bg.form.submit(document.examSignupForm)"  value="添加下一个" title="保存本个并添加下一个"/>
    [/@]
[/@]
<script language="javascript" >
    jQuery(function() {
        // workaround 学期控件上面和下面出现两个横线
        jQuery('li', document.examSignupForm).each(function(index, li){
            if(/^\s*$/g.test(jQuery(li).html())) {
                jQuery(li).remove();
            }
        });
    });
    
    var form = document.examSignupForm;
    function searchStudent(){
        jQuery.post("manage!searchStudent.action",{studentCode:$("#stdCode").val()},function(data){
            if (data == ""){
                $("#stdId").val("");
                $("#stdId").parent().find(".error").remove();
                $("#stdId").parent().append($("<label class='error' for='stdId'>查无此人!</label>"));
                $("#stdName").html("&nbsp;");
            } else {
                var dataObj = eval("(" + data + ")");
                $("#stdId").parent().find(".error").remove();
                $("#stdName").html(dataObj.student.person.formatedName);
                $("#stdId").val(dataObj.student.id);
            }
        },"text");
    }
    
   function clearNoNum(obj)
    {
        obj.value = obj.value.replace(/[^\d.]/g,"");
        obj.value = obj.value.replace(/^\./g,"");
        obj.value = obj.value.replace(/\.{2,}/g,".");
        obj.value = obj.value.replace(".","$#$").replace(/\./g,"").replace("$#$",".");
    }
</script>
[@b.foot/]
