[#ftl]
[@b.head/]
[@b.grid items=otherGrades var="otherGrade"]
    [@b.gridbar title="校外考试成绩维护"]
        bar.addItem("${b.text("action.export")}","exportData()");
        bar.addItem("打印","printted()");
    [/@]

    [@b.row]
        [@b.boxcol/]
        [@b.col property="std.code" title="std.code" width="12%"]
                [@b.a href="studentSearch!info?student.id="+otherGrade.std.id title="${b.text('info.user.std')}" ]${(otherGrade.std.code)!}[/@]
        [/@]
        [@b.col property="std.name" title="std.name" width="12%"/]
        [@b.col property="subject.name" title="考试类型" width="12%"/]
        [@b.col property="score" title="成绩" width="10%"/] 
        [@b.col property="passed" title="是否合格" width="10%"]
            [#if !(otherGrade.passed)]<font style="color:red">[/#if]
            ${(otherGrade.passed)!?string("合格","不合格")}
            [#if !(otherGrade.passed)]</font>[/#if]
        [/@]
        [@b.col property="std.department.name" title="院系"  width="12%"/]
        [@b.col property="semester.id" title="common.semester"  width="15%"]${(otherGrade.semester.schoolYear)!}(${(otherGrade.semester.name)!})[/@]
        [@b.col property="certificateNumber" title="证书编号"  width="12%"]${(otherGrade.certificateNumber)!"--"}[/@]
    [/@]
[/@]

[@b.form name="otherGradeListForm" theme="xml" target="otherGradeList"]
        <input type="hidden" name="configId" id="configId" />
        <input type="hidden" name="params" value="[@htm.queryStr /]" />
[/@]

<script>
    //导出校外考试成绩
    function exportData(){
        var otherGradeIds = bg.input.getCheckBoxValues("otherGrade.id");
        var form = action.getForm();
        if (otherGradeIds) {
            bg.form.addInput(form,"otherGradeIds",otherGradeIds);    
        }else{
            if(!confirm("是否导出查询条件内的所有数据?")) return;
                if(""!=action.page.paramstr){
                  bg.form.addHiddens(form,action.page.paramstr);
                  bg.form.addParamsInput(form,action.page.paramstr);
                }
            bg.form.addInput(form,"otherGradeIds","");
        }
        bg.form.addInput(form,"otherGradeIds",bg.input.getCheckBoxValues("otherGrade.id"));
        bg.form.addInput(form,"keys","std.code,std.name,subject.category.name,subject.name,score,scoreText,std.department.name,std.major.name,std.grade,certificateNumber");
        bg.form.addInput(form,"titles","学号,姓名,考试类型,考试科目,分数,得分等级,院系,专业,年级,证书编号");
        bg.form.addInput(form,"fileName","校外考试成绩数据");
        bg.form.submit(form,"${b.url('!export')}","_self");
    }
</script>
[@b.foot/]