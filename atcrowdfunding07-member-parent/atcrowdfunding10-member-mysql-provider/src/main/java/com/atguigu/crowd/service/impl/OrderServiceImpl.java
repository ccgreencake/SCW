package com.atguigu.crowd.service.impl;

import com.atguigu.crowd.entity.po.AddressPO;
import com.atguigu.crowd.entity.po.AddressPOExample;
import com.atguigu.crowd.entity.po.OrderPO;
import com.atguigu.crowd.entity.po.OrderProjectPO;
import com.atguigu.crowd.entity.vo.AddressVO;
import com.atguigu.crowd.entity.vo.OrderProjectVO;
import com.atguigu.crowd.entity.vo.OrderVO;
import com.atguigu.crowd.mapper.AddressPOMapper;
import com.atguigu.crowd.mapper.OrderPOMapper;
import com.atguigu.crowd.mapper.OrderProjectPOMapper;
import com.atguigu.crowd.service.api.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : LiuDongBin
 * @create 2023/8/29 16:33
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderPOMapper orderPOMapper;

    @Autowired
    private OrderProjectPOMapper orderProjectPOMapper;

    @Autowired
    private AddressPOMapper addressPOMapper;

    @Override
    public OrderProjectVO getOrderProjectVO(Integer projectId, Integer returnId) {

        return orderProjectPOMapper.selectOrderProjectVO(returnId);
    }
    @Override
    public List<AddressVO> getAddressVOList(Integer memberId) {

        AddressPOExample example = new AddressPOExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        List<AddressPO> addressPOList = addressPOMapper.selectByExample(example);

        List<AddressVO> addressVOList = new ArrayList<>();
        for (AddressPO addressPO : addressPOList) {

            AddressVO addressVO = new AddressVO();
            BeanUtils.copyProperties(addressPO, addressVO);
            addressVOList.add(addressVO);
        }
        return addressVOList;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @Override
    public void saveAddressVO(AddressVO addressVO) {

        AddressPO addressPO = new AddressPO();

        BeanUtils.copyProperties(addressVO, addressPO);

        addressPOMapper.insert(addressPO);

    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void saveOrderVO(OrderVO orderVO) {

        OrderPO orderPO = new OrderPO();

        BeanUtils.copyProperties(orderVO, orderPO);

        OrderProjectPO orderProjectPO = new OrderProjectPO();

        BeanUtils.copyProperties(orderVO.getOrderProjectVO(), orderProjectPO);

        orderPOMapper.insert(orderPO);

        // 保存OrderProjectPO需要orderPO的id作为外键
        Integer id = orderPO.getId();
        // 这里需要先修改mapper，
        orderProjectPO.setOrderId(id);

        orderProjectPOMapper.insert(orderProjectPO);
    }
}
