import React, { useState, useEffect } from "react";

// reactstrap components
import {
  Card,
  CardBody,
  Table,
  Row,
  Col
} from "reactstrap";
import { useViewContext } from "../ViewContext";
import LoginModal from '../LoginModal'

var undef;

 const Accounting = (props) => {

    const getBudget = (row,which) => {
        for (var x of props.budget) {
            if (x.id === row.id) {
                return x[which]
            }
        }
        return "??";
    }

    const AccountingView = () => {
        var data = props.values;
        data.sort(function(a, b) {
            a = a.customer_id + a.name;
            b = b.customer_id + b.name;
            return (b > a) - (b < a);
        });

        return(
          data.map((row, index) => (
            <tr key={'camps-' + index}>
              <td>{index+1}</td>
              <td className="text-right">{row.customer}</td>
              <td className="text-right">{row.id}</td>
              <td className="text-right">{row.name}</td>
              <td className="text-right">Cost={row.total_value}/Budget={getBudget(row,"total_budget")}</td>
              <td className="text-right">Cost={row.daily_value}/Budget={getBudget(row,"budget_limit_daily")}</td>
              <td className="text-right">Cost={row.hourly_value}/Budget={getBudget(row,"budget_limit_hourly")}</td>
            </tr>))
        );
      }

  return (
    <>
                <div className="row mb-3">
                    <div className="col-xl-12 col-lg-12" >
                        <strong className="h3">
                            Accounting
                        </strong>
                    </div>
                </div>
                <Card className="card-chart">
                    <CardBody>
                      <Table size="sm">
                        <thead>
                          <tr>
                            <th>#</th>
                            <th className="text-right">Customer</th>
                            <th className="text-center">ID</th>
                            <th className="text-right">Name</th>
                            <th className="text-right">Total</th>
                            <th className="text-right">Daily</th>
                            <th className="text-right">Hourly</th>
                          </tr>
                      </thead>
                      <tbody>
                       <AccountingView/>
                      </tbody>
                    </Table>
                  </CardBody>
                </Card>
    </>
  );
 }

 export default Accounting;