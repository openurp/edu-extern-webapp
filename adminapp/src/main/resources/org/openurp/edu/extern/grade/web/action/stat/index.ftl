[#ftl]
[@b.head/]
<div>
<table class="indexpanel">
<tr>
    <td class="index_view">
    [@b.form name="otherGradeStatsearchForm" action="!statGrade" title="ui.searchForm" target="otherGradeStatList" theme="search"]
        [@b.textfield name="otherGrade.std.grade" label="年级"/]
        [@b.select name="otherGrade.subject.category.id" label="考试类型" items=otherExamCategories empty="..." /]
        [@b.select name="otherGrade.subject.id" label="考试科目" items=otherExternExamSubjects empty="..." /]
    [/@]
    </td>
    <td class="index_content">
    [@b.div id="otherGradeStatList" href="!statGrade" /]
    </td>
</tr>
</table>
</div>
[@b.foot/]