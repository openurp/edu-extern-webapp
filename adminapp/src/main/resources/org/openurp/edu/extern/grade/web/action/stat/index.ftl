[#ftl]
[@b.head/]
<div>
<table class="indexpanel">
<tr>
    <td class="index_view">
    [@b.form name="examGradeStatsearchForm" action="!statGrade" title="ui.searchForm" target="examGradeStatList" theme="search"]
        [@b.textfield name="examGrade.std.grade" label="年级"/]
        [@b.select name="examGrade.subject.category.id" label="考试类型" items=examCategories empty="..." /]
        [@b.select name="examGrade.subject.id" label="考试科目" items=examSubjects empty="..." /]
    [/@]
    </td>
    <td class="index_content">
    [@b.div id="examGradeStatList" href="!statGrade" /]
    </td>
</tr>
</table>
</div>
[@b.foot/]