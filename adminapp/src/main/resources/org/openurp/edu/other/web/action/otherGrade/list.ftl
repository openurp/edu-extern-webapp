[#ftl]
[@b.head/]
[@b.grid items=otherGrades var="otherGrade"]
    [@b.gridbar title="资格考试成绩维护"]
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
        [@b.col property="std.code" title="std.code" width="12%"]
                [@b.a href="studentSearch!info?student.id="+otherGrade.std.id title="${b.text('info.user.std')}" ]${(otherGrade.std.code)!}[/@]
        [/@]
        [@b.col property="std.name" title="std.name" width="12%"/]
        [@b.col property="subject.name" title="考试科目" width="12%"/]
        [@b.col property="score" title="成绩" width="10%"/] 
        [@b.col property="passed" title="是否合格" width="10%"]
            [#if !(otherGrade.passed)]<font style="color:red">[/#if]
            ${(otherGrade.passed)!?string("合格","不合格")}
            [#if !(otherGrade.passed)]</font>[/#if]
        [/@]
        [#--][@b.col property="scoreText" title="得分等级"]${(otherGrade.scoreText)!"--"}[/@][--]
        [@b.col property="std.department.name" title="院系"  width="12%"/]
        [@b.col property="semester.id" title="common.semester"  width="15%"]${(otherGrade.semester.schoolYear)!}(${(otherGrade.semester.name)!})[/@]
        [#--][@b.col property="examNo" title="准考证号"]${(otherGrade.examNo)!"--"}[/@][--]
        [@b.col property="certificateNumber" title="证书编号"  width="12%"]${(otherGrade.certificateNumber)!"--"}[/@]
    [/@]
[/@]

[@b.form name="otherGradeListForm" target="otherGradeList"]
        <input type="hidden" name="configId" id="configId" />
        <input type="hidden" name="params" value="[@htm.queryStr /]" />
[/@]

<script>
    //导出资格考试成绩
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
        bg.form.addInput(form,"fileName","资格考试成绩数据");
        bg.form.submit(form,"${b.url('!export')}","_self");
    }
    
    function remove(){
        var otherGradeIds=bg.input.getCheckBoxValues('otherGrade.id');
        if(otherGradeIds==""){
            alert('请至少选择一条数据进行操作!');
            return;
        }
        
        if(confirm('确定要删除?')){
            bg.form.addInput(document.otherGradeListForm,"otherGradeIds",otherGradeIds);
            document.otherGradeListForm.action="otherGrade!remove.action";
            bg.form.submit(document.otherGradeListForm);
        }

    }
</script>
[@b.foot/]