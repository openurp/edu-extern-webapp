[#ftl]
[@b.head/]
  [@b.grid items=externGrades var="externGrade"]
    [@b.gridbar]
      bar.addItem("${b.text("action.new")}", action.add());
      bar.addItem("${b.text("action.modify")}", action.edit());
      bar.addItem("成绩认定", action.single("convertList"), "action-update");
      bar.addItem("${b.text("action.delete")}", action.remove("确认要删除吗？"));
      bar.addItem("${b.text("action.export")}", action.exportData("std.user.code:学号,std.user.name:姓名,school.name:校外学校,level.name:培养层次,category:教育类别,majorName:外校专业,courseName:外校课程,scoreText:外校得分,credits:外校学分,acquiredOn:获得日期,updatedAt:录入时间,courseGradeSize:已认定课数"));
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
      [@b.col title="已认定" sortable="false"  width="50px"]${externGrade.courseGrades?size}[/@]
    [/@]
  [/@]
[@b.foot/]
