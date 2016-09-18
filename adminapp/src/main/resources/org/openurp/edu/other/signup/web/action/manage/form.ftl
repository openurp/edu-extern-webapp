[#ftl/]
[@b.head/]
    [@b.toolbar title="考试报名信息"]
        bar.addBack("${b.text("action.back")}");
    [/@]
[@b.form name="otherExamSignUpForm" action="!save"  theme="list"]
    [#if (otherExamSignUp.id)?exists]
        [@b.field label="std.code"]
            ${(otherExamSignUp.std.code)!}
            <input type="hidden" name="otherExamSignUp.std.code" value="${(otherExamSignUp.std.code)!}" />
        [/@]
    [#else]
        [@b.field name="findstudent"]
            [@b.textfield theme="xml" name="otherExamSignUp.std.code" maxlength="15"  id="stdCode" label="std.code" value="${(otherExamSignUp.std.code)!}" required="true" style="width:150px" comment="<input type='button' value='查询' onClick='searchStudent()'/>"/]
             <input type="hidden" id="stdId" name="otherExamSignUp.std.id" value="${(otherExamSignUp.std.id)!}" />
        [/@]
    [/#if]
    [@b.field label="姓名"]
      <table>
       <tr>
            <td id="stdName">${(otherExamSignUp.std.name)?default("&nbsp;")}</td>
       </tr>
      </table>
    [/@]
    [#--
    [@b.textfield name="otherExamSignUp.examNo" label="准考证号码" value="${(otherExamSignUp.examNo)!}" style="width:150px" onBlur="clearNoNum(this)"/]
    --]
    [@b.textfield name="otherExamSignUp.feeOfSignUp" maxlength="10" label="报名费" value="${(otherExamSignUp.feeOfSignUp)!}" style="width:150px" onBlur="clearNoNum(this)"/]
    [@b.textfield name="otherExamSignUp.feeOfOutline" maxlength="10" label="考纲费" value="${(otherExamSignUp.feeOfOutline)!}" style="width:150px" onBlur="clearNoNum(this)"/]
    [@b.textfield name="otherExamSignUp.feeOfMaterial" maxlength="10" label="材料费" value="${(otherExamSignUp.feeOfMaterial)!}" style="width:150px" onBlur="clearNoNum(this)"/]
    [@eams.semesterCalendar label="学年" name="otherExamSignUp.semester.id" value=(otherExamSignUp.semester)! empty=false/]
    [#--
    [@eams.projectUI name="project.id" label="项目" empty="false" semesterName="otherExamSignUp.semester.id" semesterEmpty="false" semesterValue=otherExamSignUp.semester /]
    --]
    [@b.select name="otherExamSignUp.subject.id" items=otherExamSubjects empty="..." value=(otherExamSignUp.subject.id)! label="报名科目" style="width:150px" required="true"/]
    [@b.select name="otherExamSignUp.campus.id" items=campuses empty="..." value=(otherExamSignUp.campus.id)! label="考试校区" style="width:150px" required="true" /]
    [@b.field label="是否乘坐班车"]
        <select name="otherExamSignUp.takeBus" style="width:150px">
            <option value="1" [#if (otherExamSignUp.takeBus)]selected[/#if]>${b.text("common.yes")}</option>
            <option value="0" [#if (!otherExamSignUp.takeBus)]selected[/#if]>${b.text("common.no")}</option>
        </select>
    [/@]
    [#--
    [@b.datepicker label="entity.signUpAt" name="otherExamSignUp.signUpAt" value="${(otherExamSignUp.signUpAt?string('yyyy-MM-dd'))!}" format="yyyy-MM-dd" requried="true" readOnly="readOnly"/]
    --]
    [@b.formfoot]
        <input type="hidden" name="otherExamSignUpId" value="${otherExamSignUp.id!}" />
        
        [@b.submit value="action.submit"/]
        <input type="button"  onclick="bg.form.addInput(form,'addNext','1'),bg.form.submit(document.otherExamSignUpForm)"  value="添加下一个" title="保存本个并添加下一个"/>
    [/@]
[/@]
<script language="javascript" >
    jQuery(function() {
        // workaround 学期控件上面和下面出现两个横线
        jQuery('li', document.otherExamSignUpForm).each(function(index, li){
            if(/^\s*$/g.test(jQuery(li).html())) {
                jQuery(li).remove();
            }
        });
    });
    
    var form = document.otherExamSignUpForm;
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
