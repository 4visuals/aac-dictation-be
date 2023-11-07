package github.visual4.aacweb.dictation.domain.order.group;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import github.visual4.aacweb.dictation.BaseDao;
import github.visual4.aacweb.dictation.domain.order.group.GroupOrderForm.OrderFormState;
import github.visual4.aacweb.dictation.domain.user.User;

@Import({GroupOrderDao.class})
class GroupOrderDaoTest extends BaseDao{

	@Autowired
	GroupOrderDao groupOrderDao;
	
	@Test
	void test() {
		List<GroupOrderForm> orders = groupOrderDao.findRetailGroupOrderForms(GroupOrderForm.Column.group_order_state, OrderFormState.PND);
		assertThat(orders.size()).isGreaterThan(0);
		for (GroupOrderForm order : orders) {
			User sender = order.getSender();
			assertThat(sender).isNotNull();
			assertThat(sender.getSeq()).isNotNull();
			System.out.println(order);
		}
	}

}
