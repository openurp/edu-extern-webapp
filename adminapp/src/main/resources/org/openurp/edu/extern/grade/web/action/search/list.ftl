[#ftl]
[@b.head/]
[@b.grid items=examGrades var="examGrade"]
    [@b.gridbar title="校外考试成绩维护"]
        bar.addItem("${b.text("action.export")}","exportData()");
        bar.addItem("打印","printted()");
    [/@]

    [@b.row]
        [@b.boxcol/]
        [@b.col property="std.user.code" title="std.user.code" width="12%"]
                [@b.a href="studentSearch!info?student.id="+examGrade.std.id title="${b.text('info.user.std')}" ]${(examGrade.std.user.code)!}[/@]
        [/@]
        [@b.col property="std.user.name" title="std.name" width="12%"/]
        [@b.col property="subject.name" title="考试类型" width="12%"/]
        [@b.col property="scoreText" title="成绩" width="10%"/]
        [@b.col property="passed" title="是否合格" width="10%"]
            [#if !(examGrade.passed)]<font style="color:red">[/#if]
            ${(examGrade.passed)!?string("合格","不合格")}
            [#if !(examGrade.passed)]</font>[/#if]
        [/@]
        [@b.col property="std.state.department.name" title="院系"  width="12%"/]
        [@b.col property="semester.id" title="common.semester"  width="15%"]${(examGrade.semester.schoolYear)!}(${(examGrade.semester.name)!})[/@]
        [@b.col property="certificateNo" title="证书编号"  width="12%"]${(examGrade.certificateNo)!"--"}[/@]
    [/@]
[/@]

[@b.form name="examGradeListForm" theme="xml" target="examGradeList"]
        <input type="hidden" name="configId" id="configId" />
        <input type="hidden" name="params" value="[@htm.queryStr /]" />
[/@]

<script>
    //导出校外考试成绩
    function exportData(){
        var examGradeIds = bg.input.getCheckBoxValues("examGrade.id");
        var form = action.getForm();
        if (examGradeIds) {
            bg.form.addInput(form,"examGradeIds",examGradeIds);
        }else{
            if(!confirm("是否导出查询条件内的所有数据?")) return;
                if(""!=action.page.paramstr){
                  bg.form.addHiddens(form,action.page.paramstr);
                  bg.form.addParamsInput(form,action.page.paramstr);
                }
            bg.form.addInput(form,"examGradeIds","");
        }
        bg.form.addInput(form,"examGradeIds",bg.input.getCheckBoxValues("examGrade.id"));
        bg.form.addInput(form,"keys","std.user.code,std.name,subject.category.name,subject.name,score,scoreText,std.department.name,std.major.name,std.grade,certificateNo");
        bg.form.addInput(form,"titles","学号,姓名,考试类型,考试科目,分数,得分等级,院系,专业,年级,证书编号");
        bg.form.addInput(form,"fileName","校外考试成绩数据");
        bg.form.submit(form,"${b.url('!export')}","_self");
    }
</script>
[@b.foot/]
