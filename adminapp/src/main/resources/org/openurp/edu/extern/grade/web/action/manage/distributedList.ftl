[#ftl]
[@b.head/]
  [@b.toolbar title = "<span style=\"color:blue\">" + externExamGrade.std.user.name + "（<span style=\"padding-left: 1px; padding-right: 1px\">" + externExamGrade.std.user.code + "</span>）" + externExamGrade.subject.name + "</span>证书课程及成绩分配明细"]
    bar.addItem("返回", function() {
      bg.form.submit(document.examGradesearchForm);
    }, "backward.png");
  [/@]
  [@b.grid items=externExamGrade.courseGrades?sort_by(["course", "code"]) var="courseGrade" sortable="false"]
    [@b.gridbar]
      bg.form.addInput(action.getForm(), "externExamGrade.id", "${externExamGrade.id}");
      bar.addItem("分配", function() {
        bg.form.submit(action.getForm(), "${b.url("!toDistribute")}");
      }, "${b.theme.iconurl("actions/new.png")}");
      bar.addItem("取消", action.single("undistribute", "确认要取消分配吗？"), "${b.theme.iconurl("actions/edit-delete.png")}");
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="课程代码" property="course.code"/]
      [@b.col title="课程名称" property="course.name"/]
      [@b.col title="课程类别" property="courseType.name"/]
      [@b.col title="学分" property="course.credits"/]
      [@b.col title="成绩（显示）" property="score"]${courseGrade.score?string("0.#")}${("（" + courseGrade.scoreText + "）")!}[/@]
      [@b.col title="绩点" property="gp"]${courseGrade.gp?string("0.#")}[/@]
      [@b.col title="修读类别" property="courseTakeType.name"/]
      [@b.col title="考核方式" property="examMode.name"/]
      [@b.col title="是否免听" property="freeListening"]${courseGrade.freeListening?string("是", "否")}[/@]
    [/@]
  [/@]
[@b.foot/]
