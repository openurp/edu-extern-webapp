[#ftl]
[@b.head/]
  [@b.grid items=externGrades var="externGrade"]
    [@b.gridbar]
      bar.addItem("${b.text("action.new")}", action.add());
      bar.addItem("${b.text("action.modify")}", action.edit());
      bar.addItem("成绩认定", action.single("convertList"), "action-update");
      bar.addItem("${b.text("action.delete")}", action.remove("确认要删除吗？"));
      [#if externGrades.totalItems gt 10000]
        var bar1=bar.addMenu("查询结果导出", function() { 
          alert("导出数据每次不能超过10000条，建议分批导出。");
        });
        bar1.addItem("外校成绩导出", function() { 
          alert("导出数据每次不能超过10000条，建议分批导出。");
        }, "excel.png");
      [#else]
        var bar1=bar.addMenu("查询结果导出", action.exportData("std.user.code:学号,std.user.name:姓名,school.name:校外学校,level.name:培养层次,category:教育类别,majorName:外校专业,courseName:外校课程,scoreText:外校得分,credits:外校学分,acquiredOn:获得日期,updatedAt:录入时间,courseGradeSize:已认定课数"));
        bar1.addItem("外校成绩导出", action.exportData("info.std.user.name:姓名,info.std.person.code:身份证号,info.school.code:转换学校代码,original.course.code:原课程来源代码,info.majorName:原专业名称,info.courseName:原课程名称,info.school.name:原办学机构,info.level.code:原教育层次代码,info.category.code:原教育类别代码,info.credits:原学分,original.course.creditHours:原学时,info.scoreText:原成绩,info.acquiredOn:获得时间,info.std.level.code:转换后教育层次代码,courseGrade.course.code:转换后课程代码,courseGrade.course.name:转换后课程名称,courseGrade.course.credits:转换后学分", "xls", "fileName=本校${(graduateSession.name)!"所有"}批次学分银行成绩-外校成绩&dataInSource=courseGrade"), "excel.png");
      [/#if]
      bar.addItem("打印", function() {
        bg.form.submit(action.getForm(), "${b.url("!identificationReport")}", "_blank");
      }, "action-print");
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="学号" property="std.user.code"  width="95px"/]
      [@b.col title="姓名" property="std.user.name" width="70px"/]
      [@b.col title="校外学校" property="school.name"/]
      [@b.col title="培养层次" property="level.name" width="60px"/]
      [@b.col title="教育类别" property="category.name"  width="90px"/]
      [@b.col title="专业" property="majorName"  width="90px"/]
      [@b.col title="课程" property="courseName"/]
      [@b.col title="得分" property="scoreText" width="40px"/]
      [@b.col title="学分" property="credits" width="40px"/]
      [@b.col title="获得日期" property="acquiredOn" width="60px"]${externGrade.acquiredOn?string("yyyy-MM")}[/@]
      [@b.col title="已认定" sortable="false"  width="50px"]${externGrade.grades?size}[/@]
    [/@]
  [/@]
[@b.foot/]
