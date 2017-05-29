[#ftl/]
[@b.head/]
[@b.form name="examSignupListForm" action="!search" target="examSignupList"]
    [@b.grid items=examSignups var="examSignup"]
        [#if !info?exists]
        [@b.gridbar]
                bar.addItem("${b.text("action.new")}",action.add());
                bar.addItem("${b.text("action.modify")}",action.edit());
                bar.addItem("${b.text("action.delete")}",action.remove());
                
                m=bar.addMenu("${b.text("action.export")}","exportList(document.examSignupsearchForm)");
                m.addItem("按四六级格式导出","exportCET(document.examSignupsearchForm)");
                m.addItem("按计算机格式导出","exportComputer(document.examSignupsearchForm)");
                m.addItem("按普通话格式导出","exportPTH(document.examSignupsearchForm)");
                bar.addItem("${b.text("action.export")}学生照片","downloadAvatorBatch()");
                function downloadAvatorBatch(){
                    var form = document.examSignupListForm;
                    var ids = bg.input.getCheckBoxValues("examSignup.id");
                    if(ids ==null || ids ==""){
                        alert("请至少选择一条");
                        return;
                    }
                    bg.form.addInput(form,"examSignup.ids",ids);
                    bg.form.submit(form,"${b.url('manage!downloadAvatorBatch')}","_self");
                }
        [/@]
        [/#if]
        [@b.row]
            [@b.boxcol width="4%"/]
            [@b.col property="std.user.code" title="std.user.code" width="12%"]
                [@b.a target="_blank" href="studentSearch!info?student.id="+examSignup.std.id title="${b.text('info.user.std')}" ]${(examSignup.std.user.code)!}[/@]
            [/@]
            [@b.col property="std.name" title="姓名" width="10%"]
                [@b.a href="manage!info?examSignup.id="+examSignup.id title="${b.text('ui.examSignup.frame')}" ]${(examSignup.std.name)!}[/@]
            [/@]
            [@b.col property="std.state.department.name" title="院系" width="14%"/]
            [@b.col property="std.stdType.name" title="学生类别" width="8%"]${(examSignup.std.stdType.name)!}[/@]
            [@b.col property="subject.category.name" title="考试类型" width="9%"/]
            [@b.col property="subject.name" title="报名科目" width="16%"/]
            [@b.col property="campus.name" title="校区" width="9%"/]
            [@b.col property="semester.id" title="common.semester" width="10%"]${examSignup.semester.schoolYear}(${examSignup.semester.name})[/@]
            [@b.col property="signupAt" title="报名时间" width="9%"]${((examSignup.signupAt)?string("MM-dd HH:mm"))!}[/@]
        [/@]
    [/@]
[/@]
<script>
    var form = document.examSignupListForm;
    function savePayState(state){
        var ids = bg.input.getCheckBoxValues("examSignup.id");
        if(ids==""){
            alert("请选择一条记录");
            return;
        }
        bg.form.addInput(form,"examSignupIds",ids,"hidden");
        bg.form.addInput(form,"examSignup.payState.id",state,"hidden");
        form.action="manage!savePayState.action?examSignup.payState="+state;
        bg.form.submit(form);
    }    
            
    function exportList(form){
        bg.form.addInput(form, "keys", "std.user.code,std.name,std.grade,std.gender.name,std.major.name,std.major.code,std.department.name,subject.category.name,subject.name,semester.schoolYear,semester.name,feeOfSignup,feeOfMaterial,feeOfOutline,total,signupAt,campus.name");
        bg.form.addInput(form, "titles", "学号,姓名,年级,性别,专业,专业代码,所属院系,考试类型,报名科目,学年,学期,报名费,材料费,考纲费,合计,报名时间,考试校区");
        bg.form.addInput(form, "fileName", "校外考试报名数据");
        bg.form.submit(form, "manage!export.action","_self");
    }
    
    function exportCET(form){
        bg.form.addInput(form, "keys", "subject.name,std.name,std.gender.name,std.user.code,std.person.idType.name,std.person.code,std.education.name,std.duration,std.beginOn,std.grade,std.department.name,std.major.name,std.adminclass.name,std.adminclass.code");
        bg.form.addInput(form, "titles", "报考科目,姓名,性别,学号,证件类型,证件号,学历层次,学制,入学年份,年级,院系,专业,班级名称,班级代码");
        bg.form.addInput(form, "fileName", "四六级报名数据");
        bg.form.submit(form, "manage!export.action","_self");
    }
    
    function exportComputer(form){
        bg.form.addInput(form, "keys", "std.user.code,std.name,std.gender.name,std.person.idType.name,std.person.code,std.department.name,std.major.discipline.name,std.major.name,std.beginOn,std.duration,std.adminclass.name,subject.name,payState.name");
        bg.form.addInput(form, "titles", "学号,姓名,性别,证件类型,证件号码,学院名称,学科名称,专业名称,入学年份,学制,班级名称,报名科目,缴费状态");
        bg.form.addInput(form, "fileName", "计算机报名数据");
        bg.form.submit(form, "manage!export.action","_self");
        
    }
    
    function exportPTH(form){
        bg.form.addInput(form, "keys", "std.user.code,std.name,std.grade,std.gender.name,std.department.name");
        bg.form.addInput(form, "titles", "学号,姓名,年级,性别,院系");
        bg.form.addInput(form, "fileName", "普通话报名数据");
        bg.form.submit(form, "manage!export.action","_self");
    }
</script>
[@b.foot/]
