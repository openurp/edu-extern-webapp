[#ftl]
[@b.head/]
  [@b.toolbar title = "可分配的课程及成绩"]
    bar.addItem("取消返回", function() {
      bg.form.submit(document.distributeListForm);
    }, "backward.png");
  [/@]
  [@b.grid id="certScoreCourse" items=certScoreCourses var="certScoreCourse" sortable="false"]
    [@b.gridbar]
      bg.form.addInput(action.getForm(), "certification.id", "${Parameters["certification.id"]}");
      bar.addItem("分配", function() {
        action.getForm().target = "certifications";
        bg.form.submitId(action.getForm(), "certScoreCourse.id", true, "${b.url("!distribute")}");
      }, "${b.theme.iconurl("actions/new.png")}");
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="课程代码" property="course.code"/]
      [@b.col title="课程名称" property="course.name"/]
      [@b.col title="得分" property="score"/]
      [@b.col title="显示" property="scoreValue"/]
    [/@]
  [/@]
[@b.foot/]
