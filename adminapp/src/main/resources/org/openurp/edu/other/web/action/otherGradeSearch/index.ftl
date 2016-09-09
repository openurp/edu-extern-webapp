[#ftl]
[@b.head/]
[@b.toolbar title="成绩查询" /]
<div>
    <table class="indexpanel">
    <tr>
        <td class="index_view">
            [@b.form name="otherGradesearchForm" action="!search" title="ui.searchForm" target="otherGradeList" theme="search"]
                [@eams.semesterCalendar theme="search" label="学年学期" name="semester.id" empty=true value=semester /]
                <input type="hidden" name="project.id" value="${projectContext.projectId}" />
                <input type="hidden" name="otherGrade.std.project.id" value="${projectContext.projectId}" />
                [@b.textfield name="otherGrade.std.code" label="std.code"/]
                [@b.textfield name="otherGrade.std.name" label="std.name"/]
                [@b.textfield name="otherGrade.std.grade" label="std.grade"/]
                [#--][@b.textfield name="otherGrade.examNo" label="准考证号"/][--]
                [@b.textfield name="otherGrade.std.adminclass.name" label="班级名称"/]
                [@b.select name="otherGrade.subject.category.id" id="categoryId" onchange="changeSubjects()" label="考试类型" items=otherExamCategories empty="..."/]
                [@b.select name="otherGrade.subject.id" id="subjectId" label="entity.subject" items=otherExamSubjects empty="..." /]
                [@b.field label="分数区间"]<input name="from" value="" maxLength="5" onBlur="clearNoNum(this)" style="width:42px"/>-<input name="to" onBlur="clearNoNum(this)" value="" maxLength="5" style="width:42px"/>
                [/@]
                [@b.select name="otherGrade.passed" label="是否合格" items={"1":"合格", "0":"不合格"} empty="..." /]
                [@b.select name="otherGrade.std.department.id" label="common.college" items=departments?sort_by("code") empty="..." /]
            [/@]
        </td>
        <td class="index_content">
            [@b.div id="otherGradeList" href="!search?semester.id="+(semester.id)!/]
        </td>
        
    </tr>
    </table>
</div>
<script>
    function downloadTemplate() {
        var actionForm = document.otherGradesearchForm;
        actionForm.target = "_blank";
        bg.form.addInput(actionForm,"template","template/excel/资格考试成绩导入模版.xls");
        bg.form.submit(actionForm, "otherGrade!downloadTemplate.action");
        actionForm.target = "otherGradeList";
    }
    
    //打印成绩    
    function printted(){
        var otherGradeIds = bg.input.getCheckBoxValues("otherGrade.id");
        var form = action.getForm();
        if (otherGradeIds) {
            bg.form.addInput(form,"otherGradeIds",otherGradeIds);    
        }else{
            if(!confirm("是否打印查询条件内的所有数据?")) return;
                if(""!=action.page.paramstr){
                  bg.form.addHiddens(form,action.page.paramstr);
                  bg.form.addParamsInput(form,action.page.paramstr);
                }
            bg.form.addInput(form,"otherGradeIds","");
        }
        bg.form.submit(form,"${b.url('!printShow')}","_blank");
    }
    
    function changeSemester(){
        bg.form.addInput(document.otherGradesearchForm,"semesterId",jQuery("input[name='semester.id']").val());
        bg.form.submit(document.otherGradesearchForm);
    }
    
    function changeSubjects(){
        var res = jQuery.post("${base}/otherGradeSearch!categorySubject.action",{categoryId:jQuery("#categoryId").val()},function(){
            if(res.status==200){
                jQuery("#subjectId").empty();
                jQuery("#subjectId").append("<option>...</option>");
                if(res.responseText!=""){
                    jQuery("#subjectId").append(res.responseText);
                }
            }
        },"text");
    }
    
    changeSubjects();
    
    
    function clearNoNum(obj){
        obj.value = obj.value.replace(/[^\d.]/g,"");
        obj.value = obj.value.replace(/^\./g,"");
        obj.value = obj.value.replace(/\.{2,}/g,".");
        obj.value = obj.value.replace(".","$#$").replace(/\./g,"").replace("$#$",".");
    }
</script>
[@b.foot/]