package fr.picedr.bot.agenda;

import fr.picedr.bot.agenda.beans.Event;
import fr.picedr.bot.dao.DAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class AgendaDAO extends DAO {

    private Logger logger = LoggerFactory.getLogger(AgendaDAO.class);


    /**
     * get max id in agenda_entry table
     *
     * @return the max id
     */
    private int getEntryMaxId() {
        logger.debug("getEntryMaxId - start");
        int result = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String query = "SELECT MAX(id) FROM agenda_entry;";
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }

        } catch (SQLException sqle) {
            logger.error("Error while getting max id");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle) {
                logger.error("Error while closing ps or rs", sqle);
            }
        }
        logger.debug("getEntryMaxId - end : result=<%d>", result);
        return result;
    }

    /**
     * insert an agenda entry in database
     *
     * @param content  : conent of the event
     * @param date     : date of the event
     * @param type     : type of event
     * @param serverId : id of the server this event is linked to
     * @return number of inserted entry
     */
    int insertEntry(String content, Date date, String type, String serverId) {
        logger.debug("insertEntry - start : content=<" + content + "> - date=<" + date.toString() + "> - type=<" + type + "> - serverId=<" + serverId + ">");
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "INSERT INTO agenda_entry (content,date,type,serverid) values (?,?,?,?);";
            ps = conn.prepareStatement(query);
            ps.setString(1, content);
            ps.setTimestamp(2, new java.sql.Timestamp(date.getTime()));
            ps.setString(3, type);
            ps.setString(4, serverId);

            result = ps.executeUpdate();
            logger.debug("Insert result : <" + result + ">");


        } catch (SQLException sqle) {
            logger.error("Error while inserting agenda entry", sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException sqle) {
                logger.error("Error while closing ps or rs", sqle);
            }
        }

        if (result > 0) {
            result = getEntryMaxId();
        }
        logger.debug("Final result : <" + result + ">");
        return result;
    }

    /**
     * Delete an agenda entry
     *
     * @param id : id to delete
     * @return number of lines updated
     */
    int deleteEntry(int id, String serverId) {
        logger.debug("deleteEntry - start : id=<" + id + ">");
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "DELETE FROM agenda_entry WHERE id=? and serverId=?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.setString(2, serverId);
            result = ps.executeUpdate();
            if (result > 0) {
                deleteRappels(id);
            }

        } catch (SQLException sqle) {
            logger.error("Error while inserting agenda entry", sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException sqle) {
                logger.error("Error while closing ps or rs", sqle);
            }
        }
        logger.debug("deleteEntry - end : result = " + result);
        return result;
    }


    /**
     * insert an agenda rappel in database
     *
     * @param date     : date of the event
     * @param parentId : id of the entry linked to
     * @return number of line inserted
     */
    int insertRappel(int parentId, Date date) {
        logger.debug("insertRappel - start : date=<" + date.toString() + "> - parentId=<" + parentId + ">");
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "INSERT INTO agenda_rappel (date,parent) values (?,?);";
            ps = conn.prepareStatement(query);
            ps.setTimestamp(1, new java.sql.Timestamp(date.getTime()));
            ps.setInt(2, parentId);

            result = ps.executeUpdate();
            logger.debug("Insert result : <" + result + ">");


        } catch (SQLException sqle) {
            logger.error("Error while inserting agenda rappel", sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException sqle) {
                logger.error("Error while closing ps or rs", sqle);
            }
        }

        if (result > 0) {
            result = getEntryMaxId();
        }
        logger.debug("Final result : <" + result + ">");
        return result;
    }

    /**
     * Delete an agenda rappel
     *
     * @param id : id to delete
     * @return number of lines updated
     */
    int deleteRappel(int id, String serverId) {
        logger.debug("deleteRappel - start : id=<" + id + ">");
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "DELETE FROM agenda_rappel WHERE id=? AND serverid=?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            ps.setString(2, serverId);
            result = ps.executeUpdate();

        } catch (SQLException sqle) {
            logger.error("Error while inserting agenda entry", sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException sqle) {
                logger.error("Error while closing ps or rs", sqle);
            }
        }
        logger.debug("deleteRappel - end : result = " + result);
        return result;
    }

    /**
     * Delete a list of agenda rappels
     *
     * @param parentId : parent id to delete
     */
    private void deleteRappels(int parentId) {
        logger.debug("deleteRappel - start : parentId=<" + parentId + ">");
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "DELETE agenda_rappel WHERE parent=?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, parentId);
            result = ps.executeUpdate();


        } catch (SQLException sqle) {
            logger.error("Error while inserting agenda rappel", sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException sqle) {
                logger.error("Error while closing ps or rs", sqle);
            }
        }
        logger.debug("deleteRappel - end : result = " + result);
    }


    /**
     * Getting all rappels from an event
     *
     * @param parent : the event
     * @return a list of events
     */
    private List<Event> getRappels(Event parent) {
        logger.debug("getRappels - start : parent id=<" + parent.getId() + "> - parent content=<" + parent.getContent() + ">");
        List<Event> result = new ArrayList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT id,date FROM agenda_rappel WHERE parent=?;";
            ps = conn.prepareStatement(query);
            ps.setInt(1, parent.getId());
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                Date date = rs.getTimestamp("date");
                logger.debug("Adding rappel with id <" + id + ">");
                Event rappel = new Event(id, date, parent.getContent(), parent.getServerId(), parent.getType());
                result.add(rappel);
            }

        } catch (SQLException sqle) {
            logger.error("Error while getting rappels");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle) {
                logger.error("Error while closing ps or rs", sqle);
            }
        }
        logger.debug("getRappels - end");
        return result;
    }

    /**
     * Getting an event in database
     *
     * @param id of the event to get
     * @return an event
     */
    Event getEvent(int id) {

        logger.debug("getEvent - start : parent id=<" + id + ">");
        Event result = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT date,content,type,serverid FROM agenda_entry WHERE id=?;";
            ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            logger.debug("rs size = " + rs.getFetchSize());
            if (rs.next()) {
                logger.debug("rs next");
                Date date = rs.getTimestamp("date");
                String content = rs.getString("content");
                String type = rs.getString("type");
                String serverId = rs.getString("serverid");
                result = new Event(id, date, content, serverId, type);
            }
        } catch (SQLException sqle) {
            logger.error("Error while getting rappels");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle) {
                logger.error("Error while closing ps or rs", sqle);
            }
        }

        if (result != null) {
            result.setRappels(getRappels(result));
        }
        logger.debug("getEvent - end");
        return result;
    }

    /**
     * Get all events for a server
     *
     * @param serverId : server to get the events
     * @return list of Event
     */
    List<Event> getAllEvents(String serverId) {
        logger.debug("getAllEvents - start : server id=<" + serverId + ">");
        List<Event> result = new ArrayList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT id FROM agenda_entry WHERE serverid=?;";
            ps = conn.prepareStatement(query);
            ps.setString(1, serverId);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                logger.debug("Adding : " + id);
                Event event = getEvent(id);
                if (event != null) {
                    result.add(event);
                }
            }
        } catch (SQLException sqle) {
            logger.error("Error while getting all events");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle) {
                logger.error("Error while closing ps or rs", sqle);
            }
        }
        logger.debug("getAllEvents - end");
        return result;
    }

    /**
     * Get all events of the day
     *
     * @param serverId for which events are get
     * @return a list of Event
     */
    List<Event> getTodayEvents(String serverId) {
        logger.debug("getTodayEvents - start : server id=<" + serverId + ">");
        List<Event> result = new ArrayList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT id FROM agenda_entry WHERE serverid=? " +
                    "AND TO_CHAR(date,'YYYYMMDD')=TO_CHAR(current_timestamp,'YYYYMMDD')" +
                    "AND type IN ('" + AgendaService.TYPE_AGENDAD + "','" + AgendaService.TYPE_AGENDA + "');";
            ps = conn.prepareStatement(query);
            ps.setString(1, serverId);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                logger.debug("Adding : " + id);
                Event event = getEvent(id);
                if (event != null) {
                    result.add(event);
                }
            }
        } catch (SQLException sqle) {
            logger.error("Error while getting today's events");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle) {
                logger.error("Error while closing ps or rs", sqle);
            }
        }
        logger.debug("getTodayEvents - end");
        return result;
    }

    /**
     * Get today's reminders for all day events
     *
     * @param serverId : server to get the reminder for
     * @return list of Event
     */
    List<Event> getTodayRappels(String serverId) {
        logger.debug("getTodayRappels - start : server id=<" + serverId + ">");
        List<Event> result = new ArrayList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT ar.id as id,ar.date as date, ae.content as content, ae.type as type " +
                    "FROM agenda_rappel ar, agenda_entry ae " +
                    "WHERE ae.id = ar.parent " +
                    "AND serverid=? " +
                    "AND TO_CHAR(ar.date,'YYYYMMDD')=TO_CHAR(current_timestamp,'YYYYMMDD') " +
                    "AND ae.type='" + AgendaService.TYPE_AGENDAD + "';";
            ps = conn.prepareStatement(query);
            ps.setString(1, serverId);
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                Date date = rs.getTimestamp("date");
                String content = rs.getString("content");
                String type = rs.getString("type");

                Event event = new Event(id, date, content, serverId, type);
                logger.debug("Adding : " + id);
                result.add(event);

            }
        } catch (SQLException sqle) {
            logger.error("Error while getting today's events");
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle) {
                logger.error("Error while closing ps or rs", sqle);
            }
        }
        logger.debug("getTodayRappels - end");
        return result;
    }

    /**
     * Get events for a precise date
     *
     * @param date : date to get the event for
     * @return list of Event
     */
    List<Event> getNow(Date date) {
        logger.debug("getNow - start ");
        List<Event> result = new ArrayList<>();

        PreparedStatement ps = null;
        ResultSet rs = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");

        try {
            String query = "SELECT id,content,type,serverid FROM agenda_entry " +
                    "WHERE TO_CHAR(date,'YYYYMMDDHH24MI') = ?" +
                    "AND type='" + AgendaService.TYPE_AGENDA + "';";
            ps = conn.prepareStatement(query);
            ps.setString(1, sdf.format(date));
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String serverId = rs.getString("serverid");
                String content = rs.getString("content");
                String type = rs.getString("type");

                Event event = new Event(id, date, content, serverId, type);
                logger.debug("Adding : " + id);
                result.add(event);

            }
        } catch (SQLException sqle) {
            logger.error("Error while getting now", sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle) {
                logger.error("Error while closing ps or rs", sqle);
            }
        }
        logger.debug("getNow - end");
        return result;

    }

    /**
     * Get reminders that occure on a precise date
     *
     * @param date : date to get the reminder for
     * @return list of Event
     */
    List<Event> getRappelsNow(Date date) {
        logger.debug("getRappelsNow - start");
        List<Event> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String query = "SELECT ar.id as id,ar.date as date, ae.content as content, ae.type as type " +
                    "FROM agenda_rappel ar, agenda_entry ae " +
                    "WHERE ae.id = ar.parent " +
                    "AND TO_CHAR(ar.date,'YYYYMMDDHH24MI') = ?" +
                    "AND ae.type='" + AgendaService.TYPE_AGENDA + "';";
            ps = conn.prepareStatement(query);
            ps.setString(1, sdf.format(date));
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String serverId = rs.getString("serverid");
                String content = rs.getString("content");
                String type = rs.getString("type");

                Event event = new Event(id, date, content, serverId, type);
                logger.debug("Adding : " + id);
                result.add(event);

            }
        } catch (SQLException sqle) {
            logger.error("Error while getting now rappels", sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqle) {
                logger.error("Error while closing ps or rs", sqle);
            }
        }
        logger.debug("getRappelsNow - end");
        return result;
    }

    /**
     * Get all event that occure before a specific date
     *
     * @param date date before wich events will be cleared
     * @return list of Event
     */
    int clear(Date date) {
        logger.debug("clear - start");
        int result = 0;
        PreparedStatement ps = null;

        try {
            String query = "DELETE agenda_entry WHERE date<? " +
                    "AND type IN ('" + AgendaService.TYPE_AGENDA + "','" + AgendaService.TYPE_AGENDAD + "')";
            ps = conn.prepareStatement(query);
            ps.setTimestamp(1, new java.sql.Timestamp(date.getTime()));
            result = ps.executeUpdate();

            query = "DELETE agenda_rappel WHERE date<?";
            ps = conn.prepareStatement(query);
            ps.setTimestamp(1, new java.sql.Timestamp(date.getTime()));
            result = result + ps.executeUpdate();


        } catch (SQLException sqle) {
            logger.error("Error while clearing", sqle);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException sqle) {
                logger.error("Error while closing ps or rs", sqle);
            }
        }
        logger.debug("clear - end : result = " + result);
        return result;
    }

}