[#ftl]
[@b.head/]
[@b.grid items=examGrades var="examGrade"]
    [@b.gridbar title="校外考试成绩维护"]
    [#if !info?exists]
        bar.addItem("${b.text("action.new")}",action.add());
        bar.addItem("${b.text("action.modify")}",action.edit());
        bar.addItem("${b.text("action.delete")}",'remove()');

        var bar1=bar.addMenu("导入导出");
        bar1.addItem("${b.text("action.import")}","importForm()");
        bar1.addItem("下载模板","downloadTemplate()");
        bar1.addItem("${b.text("action.export")}","exportData()");
        
        bar.addItem("打印","printted()");
            [/#if]
    [/@]
    [@b.row]
        [@b.boxcol/]
        [@b.col property="std.code" title="std.code" width="13%"/]
        [@b.col property="std.person.formatedName" title="姓名" width="11%"/]
        [@b.col property="subject.name" title="考试科目" width="14%"/]
        [@b.col property="scoreText" title="成绩" width="10%"/]
        [@b.col property="passed" title="是否合格" width="8%"]
            [#if !(examGrade.passed)]<font style="color:red">[/#if]
            ${(examGrade.passed)!?string("是","否")}
            [#if !(examGrade.passed)]</font>[/#if]
        [/@]
        [@b.col property="std.state.department.name" title="院系"  width="12%"/]
        [@b.col property="semester.id" title="common.semester"  width="12%"]${(examGrade.semester.schoolYear)!}(${(examGrade.semester.name)!})[/@]
        [@b.col property="certificateNo" title="证书编号"  width="15%"]${(examGrade.certificateNo)!"--"}[/@]
    [/@]
[/@]

[@b.form name="examGradeListForm" target="examGradeList"]
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
        bg.form.addInput(form,"keys","std.code,std.name,subject.category.name,subject.name,score,scoreText,std.department.name,std.major.name,std.grade,certificateNo,examOn,examNo");
        bg.form.addInput(form,"titles","学号,姓名,考试类型,考试科目,分数,得分等级,院系,专业,年级,证书编号,考试日期,准考证号");
        bg.form.addInput(form,"fileName","校外考试成绩数据");
        bg.form.submit(form,"${b.url('!export')}","_self");
    }
    
    function remove(){
        var examGradeIds=bg.input.getCheckBoxValues('examGrade.id');
        if(examGradeIds==""){
            alert('请至少选择一条数据进行操作!');
            return;
        }
        
        if(confirm('确定要删除?')){
            bg.form.addInput(document.examGradeListForm,"examGradeIds",examGradeIds);
            document.examGradeListForm.action="manage!remove.action";
            bg.form.submit(document.examGradeListForm);
        }

    }
</script>
[@b.foot/]