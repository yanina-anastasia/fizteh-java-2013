package ru.fizteh.fivt.students.yaninaAnastasia.filemap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CommandCommitServlet extends HttpServlet {
    private TransactionWorker manager;

    public CommandCommitServlet(TransactionWorker manager) {
        this.manager = manager;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getParameterMap().isEmpty())
        {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There is no table name");
            return;
        }
        String transactionId = request.getParameter("tid");
        if (transactionId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "There is no transaction id");
            return;
        }

        Transaction transaction = manager.getTransaction(transactionId);
        if (transaction == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Transaction was not found");
            return;
        }

        try {
            int diff = transaction.commit();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF8");
            response.getWriter().println("diff=" + diff);
        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
