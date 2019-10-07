[#ftl]
[@b.head/]
[@b.toolbar title="校外考试成绩维护"]
    bar.addBack("${b.text("action.back")}");
[/@]
[@b.form name="examGradeForm" action="!save" theme="list"]
    [#if (examGrade.std.id)?exists]
        [@b.field label="std.user.code"]${(examGrade.std.user.code)}[/@]
    [#else]
        [@b.field name="findstudent"]
            [@b.textfield   theme="html" name="examGrade.std.user.code" maxlength="15"  id="stdCode" label="std.user.code" value="${(examGrade.std.user.code)!}" required="true" style="width:150px" comment="<input type='button' value='查询' onClick='searchStudent()'/>"/]
            <input type="hidden" id="stdId" name="examGrade.std.id" value="${(examGrade.std.id)!}"/>
        [/@]
    [/#if]
    [@b.field label="姓名"]
      <table>
       <tr>
            <td id="stdName">${(examGrade.std.name)?default("&nbsp;")}</td>
       </tr>
      </table>
    [/@]
    [@b.select name="examGrade.subject.id" items=examSubjects?sort_by("name") empty="..." required="true" label="考试科目" value=(examGrade.subject.id)! style="width:150px"/]
    [@b.select name="examGrade.gradingMode.id" items=gradingModes label="记录方式" empty="..." required="true" value=(examGrade.gradingMode.id)! style="width:150px"/]
    [@b.select name="examGrade.examStatus.id" items=examStatuses label="考试情况" required="true" value=(examGrade.examStatus.id)! style="width:150px"/]
    [@b.textfield name="examGrade.scoreText" maxlength="5" label="成绩"  onchange="setScore(this.value)" value=(examGrade.scoreText)! required="true" style="width:150px"/]
    [@b.textfield name="examGrade.score" maxlength="5" label="分数" check="match('number')" value=(examGrade.score)! style="width:150px" comment="须数字"/]
    [@b.radios name="examGrade.passed" items={"1":"合格", "0":"不合格"} label="是否合格" value=(examGrade.passed)!true?string("1", "0")/]
    [@b.datepicker label="考试日期" name="examGrade.acquiredOn" value=(examGrade.acquiredOn)! required="true"/]
    [@b.textfield name="examGrade.examNo" label="准考证号码" value=(examGrade.examNo)! style="width:150px"/]
    [@b.textfield name="examGrade.certificate" label="证书编号" value=(examGrade.certificate)! maxlength="100" style="width:150px"/]
    [@b.formfoot]
        <input type="hidden" name="examGrade.id" value="${(examGrade.id)!}" />
        <input type="hidden" name="_params" value="${b.paramstring}" />
        [@b.submit value="action.submit"/]
    [/@]
[/@]
<script language="javascript" >
    var form = document.examGradeForm;
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
                $("#stdName").html(dataObj.student.name);
                $("#stdId").val(dataObj.student.id);
            }
        },"text");
    }

    function setScore(v){
      if(!isNaN(Number.parseFloat(v))){
        form['examGrade.score'].value=v;
      }
    }
</script>
[@b.foot/]
